package kr.hhplus.be.domain.shared.event;

import java.util.List;

import kr.hhplus.be.domain.shared.event.dto.PricedOrderItemInfo;

public record StockDecreasedEvent(
	Long orderId,
	Long userId,
	Long userCouponId,
	Long totalOriginalPrice,
	List<PricedOrderItemInfo> pricedOrderItems
) {}