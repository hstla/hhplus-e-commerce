package kr.hhplus.be.server.config.jpa.coupon.compnent.discount;

import org.springframework.stereotype.Component;

@Component("PERCENT")
public class PercentDiscountPolicy implements DiscountPolicy {
	@Override
	public long calculateDiscount(long totalPrice, long discountValue) {
		return totalPrice * discountValue / 100;
	}
}