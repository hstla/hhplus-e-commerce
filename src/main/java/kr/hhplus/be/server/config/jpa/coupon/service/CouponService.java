package kr.hhplus.be.server.config.jpa.coupon.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.coupon.compnent.UserCouponValidator;
import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.DiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final UserCouponRepository userCouponRepository;
	private final CouponRepository couponRepository;
	private final UserCouponValidator userCouponValidator;
	private final Map<CouponType, DiscountPolicy> discountPolicies;

	@Transactional
	public long useUserCoupon(long userCouponId, long totalAmount, LocalDateTime now) {
		UserCoupon findUserCoupon = userCouponRepository.findById(userCouponId);
		findUserCoupon.use(now);
		userCouponRepository.save(findUserCoupon);

		Coupon findCoupon = couponRepository.findById(findUserCoupon.getCouponId());
		findCoupon.validateNotExpired(now);

		DiscountPolicy discountPolicy = discountPolicies.get(findCoupon.getDiscountType());
		return discountPolicy.calculateDiscount(totalAmount, findCoupon.getDiscountValue());
	}

	@Transactional
	public Coupon publishCoupon(Long userId, Long couponId, LocalDateTime now) {
		Coupon coupon = couponRepository.findById(couponId);
		userCouponValidator.validateUserDoesNotHaveCoupon(userId, couponId);
		coupon.validateForPublish(now);
		coupon.decreaseQuantity();

		UserCoupon published = UserCoupon.publish(userId, couponId, now);
		userCouponRepository.save(published);
		return couponRepository.save(coupon);
	}
}