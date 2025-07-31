package kr.hhplus.be.server.config.jpa.api.payment.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.api.payment.usecase.PaymentCommand;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRequest {

	@Getter
	@NoArgsConstructor
	public static class Payment {
		@NotNull @PositiveOrZero Long orderId;
		@NotNull @PositiveOrZero Long userId;
		@NotNull PaymentType type;

		private Payment(Long orderId, Long userId, PaymentType type) {
			this.orderId = orderId;
			this.userId = userId;
			this.type = type;
		}

		public static Payment of(Long orderId, Long userId, PaymentType type) {
			return new Payment(orderId, userId, type);
		}

		public PaymentCommand.Pay toCommand() {
			return PaymentCommand.Pay.of(orderId, userId, type);
		}
	}
}