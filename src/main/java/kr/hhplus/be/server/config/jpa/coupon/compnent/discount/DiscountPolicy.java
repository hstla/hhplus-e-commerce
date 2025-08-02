package kr.hhplus.be.server.config.jpa.coupon.compnent.discount;

public interface DiscountPolicy {
	long calculateDiscount(long totalPrice, long discountValue);
}