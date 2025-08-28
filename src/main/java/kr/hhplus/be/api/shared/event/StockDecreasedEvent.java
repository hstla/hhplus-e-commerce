package kr.hhplus.be.api.shared.event;

import java.util.List;

import kr.hhplus.be.api.shared.event.dto.PricedOrderItemInfo;

public record StockDecreasedEvent(
	Long orderId,
	Long userId,
	Long userCouponId,
	Long totalOriginalPrice,
	List<PricedOrderItemInfo> pricedOrderItems
) {}