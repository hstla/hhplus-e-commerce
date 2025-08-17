package kr.hhplus.be.api.payment.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.api.payment.usecase.PaymentCommand;
import kr.hhplus.be.domain.payment.model.PaymentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRequest {

	public record Payment (
		@NotNull @PositiveOrZero Long orderId,
		@NotNull @PositiveOrZero Long userId,
		@NotNull PaymentType type
	) {
		public static Payment of(Long orderId, Long userId, PaymentType type) {
			return new Payment(orderId, userId, type);
		}
		public PaymentCommand.Pay toCommand() {
			return PaymentCommand.Pay.of(orderId, userId, type);
		}
	}
}