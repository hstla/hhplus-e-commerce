package kr.hhplus.be.server.config.jpa.payment.dto;

import kr.hhplus.be.server.config.jpa.payment.PaymentStatus;

public record PaymentResponse(
	Long paymentId,
	Long orderId,
	PaymentStatus status,
	int amount
) {
}
