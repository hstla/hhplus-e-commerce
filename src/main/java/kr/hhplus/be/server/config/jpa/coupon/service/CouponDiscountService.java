package kr.hhplus.be.server.config.jpa.coupon.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.DiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponDiscountService {

	private final Map<CouponType, DiscountPolicy> discountPolicies;

	public long calculateDiscount(UserCoupon userCoupon, Coupon findCoupon, long originPrice, LocalDateTime now) {
		userCoupon.use(now);
		DiscountPolicy discountPolicy = discountPolicies.get(findCoupon.getDiscountType());
		return discountPolicy.calculateDiscount(originPrice, findCoupon.getDiscountValue());
	}
}