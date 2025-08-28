package kr.hhplus.be.api.shared.event;

import java.util.Map;

public record OrderCompletedEvent(
	Long orderId,
	Map<Long, Integer> productOrderCounts
) {}