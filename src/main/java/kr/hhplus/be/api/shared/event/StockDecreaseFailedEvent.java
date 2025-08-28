package kr.hhplus.be.api.shared.event;

public record StockDecreaseFailedEvent(
	Long orderId
) {}