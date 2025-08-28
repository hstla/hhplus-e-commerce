package kr.hhplus.be.api.shared.event;

import java.util.List;

import kr.hhplus.be.api.shared.event.dto.PricedOrderItemInfo;

public record CouponUsedEvent(
	Long orderId,
	Long totalOriginalPrice,
	Long discountPrice,
	List<PricedOrderItemInfo> pricedOrderItems
) {}