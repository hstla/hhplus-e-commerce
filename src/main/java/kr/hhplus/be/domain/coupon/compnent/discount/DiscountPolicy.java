package kr.hhplus.be.domain.coupon.compnent.discount;

public interface DiscountPolicy {
	long calculateDiscount(long totalPrice, long discountValue);
}