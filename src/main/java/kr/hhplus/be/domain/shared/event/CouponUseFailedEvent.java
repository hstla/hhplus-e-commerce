package kr.hhplus.be.domain.shared.event;

import java.util.List;

import kr.hhplus.be.domain.shared.event.dto.PricedOrderItemInfo;

public record CouponUseFailedEvent(
	Long orderId,
	List<PricedOrderItemInfo> productOptionItems
) {}