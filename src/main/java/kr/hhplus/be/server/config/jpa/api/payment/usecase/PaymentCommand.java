package kr.hhplus.be.server.config.jpa.api.payment.usecase;

import kr.hhplus.be.server.config.jpa.payment.model.PaymentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

	@Getter
	@NoArgsConstructor
	public static class Pay {
		Long orderId;
		Long userId;
		PaymentType type;

		private Pay(Long orderId, Long userId, PaymentType type) {
			this.orderId = orderId;
			this.userId = userId;
			this.type = type;
		}

		public static Pay of(Long orderId, Long userId, PaymentType type) {
			return new Pay(orderId, userId, type);
		}
	}
}
