package kr.hhplus.be.server.config.jpa.payment.adapter.in.dto;

import kr.hhplus.be.server.config.jpa.payment.domain.PaymentStatus;

public record PaymentResponse(
	Long paymentId,
	Long orderId,
	PaymentStatus status,
	int amount
) {
}
