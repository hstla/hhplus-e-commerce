package kr.hhplus.be.server.config.jpa.payment.application;

import kr.hhplus.be.server.config.jpa.payment.domain.PaymentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

	@Getter
	@NoArgsConstructor
	public static class Pay {
		Long orderId;
		PaymentType type;

		private Pay(Long orderId, PaymentType type) {
			this.orderId = orderId;
			this.type = type;
		}

		public static Pay of(Long orderId, PaymentType type) {
			return new Pay(orderId, type);
		}
	}
}
