package kr.hhplus.be.global.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.hhplus.be.domain.coupon.compnent.discount.DiscountPolicy;
import kr.hhplus.be.domain.coupon.compnent.discount.FixedDiscountPolicy;
import kr.hhplus.be.domain.coupon.compnent.discount.PercentDiscountPolicy;
import kr.hhplus.be.domain.coupon.model.CouponType;

@Configuration
public class CouponConfig {

	@Bean
	public Map<CouponType, DiscountPolicy> discountPolicies() {
		Map<CouponType, DiscountPolicy> policies = new HashMap<>();
		policies.put(CouponType.FIXED, new FixedDiscountPolicy());
		policies.put(CouponType.PERCENT, new PercentDiscountPolicy());
		return policies;
	}
}