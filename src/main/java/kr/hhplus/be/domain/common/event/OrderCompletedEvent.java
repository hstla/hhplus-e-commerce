package kr.hhplus.be.domain.common.event;

import java.util.Map;

public record OrderCompletedEvent(
	Long orderId,
	Map<Long, Integer> productOrderCounts
) {}