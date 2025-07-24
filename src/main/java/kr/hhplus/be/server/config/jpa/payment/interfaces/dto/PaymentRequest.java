package kr.hhplus.be.server.config.jpa.payment.interfaces.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.payment.domain.PaymentType;

public record CreatePaymentRequest(
	@NotNull @PositiveOrZero Long orderId,
	@NotNull PaymentType type
) {
}
