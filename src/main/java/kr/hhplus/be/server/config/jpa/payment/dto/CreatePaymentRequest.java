package kr.hhplus.be.server.config.jpa.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.payment.PaymentType;

public record CreatePaymentRequest(
	@NotNull @PositiveOrZero Long orderId,
	@NotNull PaymentType type
) {
}
