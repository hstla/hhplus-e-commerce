package kr.hhplus.be.server.config.jpa.order.interfaces;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.order.domain.OrderStatus;

public record OrderResponse(
	Long orderId,
	int totalAmount,
	int paymentRequired,
	OrderStatus orderStatus,
	LocalDateTime createAt
	) {
}
