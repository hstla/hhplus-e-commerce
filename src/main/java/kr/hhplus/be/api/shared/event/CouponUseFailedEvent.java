package kr.hhplus.be.api.shared.event;

import java.util.List;

import kr.hhplus.be.api.shared.event.dto.PricedOrderItemInfo;

public record CouponUseFailedEvent(
	Long orderId,
	List<PricedOrderItemInfo> productOptionItems
) {}