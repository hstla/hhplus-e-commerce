package kr.hhplus.be.server.config.jpa.order.domain.discount;

public interface DiscountPolicy {
	long calculateDiscount(long totalPrice, long discountValue);
}
