package kr.hhplus.be.server.config.jpa.coupon.compnent.discount;

import org.springframework.stereotype.Component;

@Component("FIXED")
public class FixedDiscountPolicy implements DiscountPolicy {
	@Override
	public long calculateDiscount(long totalPrice, long discountValue) {
		return discountValue;
	}
}