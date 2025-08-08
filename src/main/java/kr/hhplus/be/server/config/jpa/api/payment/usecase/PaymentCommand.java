package kr.hhplus.be.server.config.jpa.api.payment.usecase;

import kr.hhplus.be.server.config.jpa.payment.model.PaymentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

	public record Pay (
		Long orderId,
		Long userId,
		PaymentType type
	) {
		public static Pay of(Long orderId, Long userId, PaymentType type) {
			return new Pay(orderId, userId, type);
		}
	}
}
