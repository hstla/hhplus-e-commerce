package kr.hhplus.be.domain.common.event.dto;

public record PricedOrderItemInfo(
	Long productOptionId,
	Long productId,
	int quantity,
	Long price,
	String name
) {
	public long calculateItemTotalPrice() {
		return price * quantity;
	}
}