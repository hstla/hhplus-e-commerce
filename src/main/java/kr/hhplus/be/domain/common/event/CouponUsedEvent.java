package kr.hhplus.be.domain.common.event;

import java.util.List;

import kr.hhplus.be.domain.common.event.dto.PricedOrderItemInfo;

public record CouponUsedEvent(
	Long orderId,
	Long totalOriginalPrice,
	Long discountPrice,
	List<PricedOrderItemInfo> pricedOrderItems
) {}