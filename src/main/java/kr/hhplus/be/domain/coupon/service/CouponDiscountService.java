package kr.hhplus.be.domain.coupon.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import kr.hhplus.be.domain.coupon.compnent.discount.DiscountPolicy;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponDiscountService {

	private final Map<CouponType, DiscountPolicy> discountPolicies;

	public long calculateDiscount(Coupon findCoupon, long originPrice) {
		DiscountPolicy discountPolicy = discountPolicies.get(findCoupon.getDiscountType());
		return discountPolicy.calculateDiscount(originPrice, findCoupon.getDiscountValue());
	}
}