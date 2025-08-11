package kr.hhplus.be.server.config.jpa.api.order.usecase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderResult;
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
import kr.hhplus.be.server.config.jpa.api.order.usecase.helper.ProductOptionStockLockManager;
import kr.hhplus.be.server.config.jpa.user.component.UserValidator;
import kr.hhplus.be.server.config.jpa.usercoupon.component.UserCouponValidator;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

	private final UserValidator userValidator;
	private final OrderPriceCalculator orderPriceCalculator;
	private final UserCouponRepository userCouponRepository;
	private final CouponRepository couponRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;

	private final ProductOptionStockLockManager productOptionStockService;
	private final CouponDiscountService couponDiscountService;

	@Transactional
	public OrderResult.Order execute(OrderCommand.Order command) {
		LocalDateTime now = LocalDateTime.now();
		userValidator.validateExistingUser(command.userId());

		List<ProductOptionSnapshot> snapshots = new ArrayList<>();
		long originPrice = 0L;
		for (OrderCommand.OrderProduct op : command.orderItemRequests()) {
			ProductOption option = productOptionStockService.decreaseStockWithLock(op.productOptionId(), op.quantity());

			ProductOptionSnapshot snapshot = ProductOptionSnapshot.create(option.getName(), op.quantity(), option.getPrice());
			originPrice += snapshot.calculateOriginPrice();
			snapshots.add(snapshot);
		}

		long discountPrice = 0L;
		// 쿠폰 사용 안할 시 discountPrice = 0
		if (command.userCouponId() != null) {
			UserCoupon userCoupon = userCouponRepository.findById(command.userCouponId());
			userCoupon.validateOwnerShip(command.userId());
			Coupon findCoupon = couponRepository.findById(userCoupon.getCouponId());
			findCoupon.validateNotExpired(now);

			discountPrice = couponDiscountService.calculateDiscount(userCoupon, findCoupon, originPrice, now);
			userCouponRepository.save(userCoupon);
		}
		long totalPrice = orderPriceCalculator.calculateTotalPrice(originPrice, discountPrice);

		Order order = Order.create(command.userId(), command.userCouponId(), originPrice, discountPrice, totalPrice, now);
		Order savedOrder = orderRepository.save(order);

		for (ProductOptionSnapshot snapshot : snapshots) {
			OrderProduct orderProduct = OrderProduct.create(command.userId(), snapshot);
			orderProductRepository.save(orderProduct);
		}
		OrderInfo.Info info = OrderInfo.Info.of(savedOrder);
		return OrderResult.Order.of(info);
	}
}