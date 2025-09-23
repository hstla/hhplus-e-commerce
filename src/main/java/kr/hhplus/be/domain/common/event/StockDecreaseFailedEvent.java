package kr.hhplus.be.domain.common.event;

public record StockDecreaseFailedEvent(
	Long orderId
) {}