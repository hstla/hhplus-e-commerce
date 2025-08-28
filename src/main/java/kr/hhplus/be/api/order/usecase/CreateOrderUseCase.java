package kr.hhplus.be.api.order.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import kr.hhplus.be.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.api.product.usecase.helper.ProductOptionStockSpinLockManager;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.coupon.service.CouponDiscountService;
import kr.hhplus.be.domain.order.component.OrderPriceCalculator;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderProduct;
import kr.hhplus.be.domain.order.model.ProductOptionSnapshot;
import kr.hhplus.be.domain.order.repository.OrderProductRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.order.service.OrderInfo;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
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
	private final RedisTemplate<String, Long> redisTemplate;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");
	Map<Long, Integer> productOrderCounts = new LinkedHashMap<>();

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

					discountPrice = couponDiscountService.calculateDiscount(findCoupon, totalOriginPrice);
					userCoupon.use(now);
					userCouponRepository.save(userCoupon);
				}

				long totalPrice = orderPriceCalculator.calculateTotalPrice(totalOriginPrice, discountPrice);

				Order order = Order.create(command.userId(), command.userCouponId(), totalOriginPrice, discountPrice, totalPrice, now);
				Order savedOrder = orderRepository.save(order);

				for (ProductOptionSnapshot snapshot : snapshots) {
					OrderProduct orderProduct = OrderProduct.create(command.userId(), snapshot);
					orderProductRepository.save(orderProduct);

					ProductOption option = lockedOptions.get(snapshot.getProductOptionId());
					Long productId = option.getProductId();
					productOrderCounts.merge(productId, snapshot.getStock(), Integer::sum);
				}
				// todo 결제 완료 알림톡 발송 및 레디스에 저장하는 건 이벤트로 바꾸기
				updateProductRankingCache(productOrderCounts);

				return OrderResult.Order.of(OrderInfo.OrderDetail.of(savedOrder));
			});
		} catch (Exception e) {
			log.error("주문 처리 실패, 재고 보상 실행", e);
			// 재고 보상
			productOptionStockSpinLockManager.compensateStocks(optionQuantities);
			throw e;
		}
	}

	/**
	 * 분산 락 트랜잭션(재고감소)
	 * 트랜잭션(
	 * userCoupon 쿠폰 있는지 없는지 확인하고 사용표시
	 * order 생성 - 본인 프로젝트
	 *)
	 * 이후 레디스 랭킹 추가 및 외부 api 생성 후 요청함. 이벤트
	 */

	private void updateProductRankingCache(Map<Long, Integer> productOrderCounts) {
		String todayKey = "hhplus:cache:product:sales:" + LocalDateTime.now().format(FORMATTER);

		for (Map.Entry<Long, Integer> entry : productOrderCounts.entrySet()) {
			redisTemplate.opsForZSet().incrementScore(todayKey, entry.getKey(), entry.getValue());
		}
		// TTL 3일 설정
		redisTemplate.expireAt(todayKey, LocalDate.now().plusDays(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
}