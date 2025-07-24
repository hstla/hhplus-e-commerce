package kr.hhplus.be.server.config.jpa.payment.interfaces.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.payment.application.PaymentCommand;
import kr.hhplus.be.server.config.jpa.payment.domain.PaymentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRequest {

	@Getter
	@NoArgsConstructor
	public static class Payment {
		@NotNull @PositiveOrZero Long orderId;
		@NotNull PaymentType type;

		private Payment(Long orderId, PaymentType type) {
			this.orderId = orderId;
			this.type = type;
		}

		private static Payment of(Long orderId, PaymentType type) {
			return new Payment(orderId, type);
		}

		public PaymentCommand.Pay toCommand() {
			return PaymentCommand.Pay.of(orderId, type);
		}
	}
}