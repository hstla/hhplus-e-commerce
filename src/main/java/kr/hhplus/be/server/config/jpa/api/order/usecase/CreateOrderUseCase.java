package kr.hhplus.be.server.config.jpa.api.order.usecase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.server.config.jpa.api.order.usecase.helper.ProductOptionStockSpinLockManager;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.service.CouponDiscountService;
import kr.hhplus.be.server.config.jpa.order.component.OrderPriceCalculator;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.model.ProductOptionSnapshot;
import kr.hhplus.be.server.config.jpa.order.repository.OrderProductRepository;
import kr.hhplus.be.server.config.jpa.order.repository.OrderRepository;
import kr.hhplus.be.server.config.jpa.order.service.OrderInfo;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateOrderUseCase {

	private final OrderPriceCalculator orderPriceCalculator;
	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponRepository couponRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;

	private final ProductOptionStockSpinLockManager productOptionStockSpinLockManager;
	private final CouponDiscountService couponDiscountService;
	private final TransactionTemplate transactionTemplate;

	public OrderResult.Order execute(OrderCommand.Order command) {
		LocalDateTime now = LocalDateTime.now();
		userRepository.assertUserExists(command.userId());

		Map<Long, Integer> optionQuantities = new LinkedHashMap<>();
		for (OrderCommand.OrderProduct op : command.orderItemRequests()) {
			optionQuantities.put(op.productOptionId(), op.quantity());
		}
		// 1. 락(트랜잭션(상품 재고 감소))
		Map<Long, ProductOption> lockedOptions = productOptionStockSpinLockManager.decreaseStockWithMultiSpinLock(optionQuantities);

		List<ProductOptionSnapshot> snapshots = new ArrayList<>();
		long originPrice = 0L;
		for (OrderCommand.OrderProduct op : command.orderItemRequests()) {
			ProductOption option = lockedOptions.get(op.productOptionId());
			ProductOptionSnapshot snapshot = ProductOptionSnapshot.create(option.getId(), option.getName(), op.quantity(), option.getPrice());
			originPrice += snapshot.calculateOriginPrice();
			snapshots.add(snapshot);
		}

		try {
			long totalOriginPrice = originPrice;
			// 2. 트랜잭션(쿠폰, 오더, 오더상품 생성)
			return transactionTemplate.execute(status -> {
				long discountPrice = 0L;

				if (command.userCouponId() != null) {
					UserCoupon userCoupon = userCouponRepository.findById(command.userCouponId());
					userCoupon.validateOwnerShip(command.userId());

					Coupon findCoupon = couponRepository.findById(userCoupon.getCouponId());
					findCoupon.validateNotExpired(now);

					discountPrice = couponDiscountService.calculateDiscount(userCoupon, findCoupon, totalOriginPrice, now);
					userCouponRepository.save(userCoupon);
				}

				long totalPrice = orderPriceCalculator.calculateTotalPrice(totalOriginPrice, discountPrice);

				Order order = Order.create(command.userId(), command.userCouponId(), totalOriginPrice, discountPrice, totalPrice, now);
				Order savedOrder = orderRepository.save(order);

				for (ProductOptionSnapshot snapshot : snapshots) {
					OrderProduct orderProduct = OrderProduct.create(command.userId(), snapshot);
					orderProductRepository.save(orderProduct);
				}
				return OrderResult.Order.of(OrderInfo.OrderDetail.of(savedOrder));
			});
		} catch (Exception e) {
			log.error("주문 처리 실패, 재고 보상 실행", e);
			// 재고 보상
			productOptionStockSpinLockManager.compensateStocks(optionQuantities);
			throw e;
		}
	}
}