package kr.hhplus.be.domain.coupon.compnent.discount;

public class FixedDiscountPolicy implements DiscountPolicy {
	@Override
	public long calculateDiscount(long totalPrice, long discountValue) {
		return discountValue;
	}
}