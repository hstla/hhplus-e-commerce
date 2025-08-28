package kr.hhplus.be.api.shared.event.dto;

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