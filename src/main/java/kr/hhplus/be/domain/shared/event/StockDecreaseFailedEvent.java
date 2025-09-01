package kr.hhplus.be.domain.shared.event;

public record StockDecreaseFailedEvent(
	Long orderId
) {}