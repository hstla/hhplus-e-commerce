package kr.hhplus.be.domain.coupon.compnent.discount;

public class PercentDiscountPolicy implements DiscountPolicy {
	@Override
	public long calculateDiscount(long totalPrice, long discountValue) {
		return totalPrice * discountValue / 100;
	}
}