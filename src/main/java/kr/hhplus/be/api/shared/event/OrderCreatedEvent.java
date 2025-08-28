package kr.hhplus.be.api.shared.event;

import java.util.List;

import kr.hhplus.be.api.shared.event.dto.OrderRequestItemInfo;

public record OrderCreatedEvent(
	Long orderId,
	Long userId,
	Long userCouponId,
	List<OrderRequestItemInfo> orderItems
) {}