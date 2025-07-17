package kr.hhplus.be.server.config.jpa.order.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.order.OrderStatus;

public record OrderResponse(
	Long orderId,
	int totalAmount,
	int paymentRequired,
	OrderStatus orderStatus,
	LocalDateTime createAt
	) {
}
