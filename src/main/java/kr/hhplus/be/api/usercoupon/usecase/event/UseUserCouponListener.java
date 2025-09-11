package kr.hhplus.be.api.usercoupon.usecase.event;

import static org.springframework.transaction.annotation.Propagation.*;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.coupon.service.CouponDiscountService;
import kr.hhplus.be.domain.shared.event.CouponUseFailedEvent;
import kr.hhplus.be.domain.shared.event.CouponUsedEvent;
import kr.hhplus.be.domain.shared.event.StockDecreasedEvent;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UseUserCouponListener {

	private final ApplicationEventPublisher eventPublisher;
	private final UserCouponRepository userCouponRepository;
	private final CouponRepository couponRepository;
	private final CouponDiscountService couponDiscountService;

	@Transactional(propagation = REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public long handleOrderCreated(StockDecreasedEvent event) {
		log.info("CouponUsedEvent 실행: {}", event.orderId());
		long discountPrice = 0L;
		LocalDateTime now = LocalDateTime.now();

		try {
			if (event.userCouponId() == null) {
				eventPublisher.publishEvent(
					new CouponUsedEvent(event.orderId(), event.totalOriginalPrice(),
						discountPrice, event.pricedOrderItems()));
				return discountPrice;
			}

			UserCoupon userCoupon = userCouponRepository.findById(event.userCouponId());
			userCoupon.validateOwnerShip(event.userId());

			Coupon findCoupon = couponRepository.findById(userCoupon.getCouponId());
			findCoupon.validateNotExpired(now);

			discountPrice = couponDiscountService.calculateDiscount(findCoupon, event.totalOriginalPrice());
			userCoupon.use(now);
			userCouponRepository.save(userCoupon);

			eventPublisher.publishEvent(
				new CouponUsedEvent(event.orderId(), event.totalOriginalPrice(), discountPrice,
					event.pricedOrderItems()));

		} catch (Exception e) {
			log.warn("쿠폰 사용 실패: {}", e);
			eventPublisher.publishEvent(new CouponUseFailedEvent(event.orderId(), event.pricedOrderItems()));
		}
		return discountPrice;
	}
}