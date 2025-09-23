package kr.hhplus.be.domain.common.event;

import java.util.List;

import kr.hhplus.be.domain.common.event.dto.OrderRequestItemInfo;

public record OrderCreatedEvent(
	Long orderId,
	Long userId,
	Long userCouponId,
	List<OrderRequestItemInfo> orderItems
) {}