package kr.hhplus.be.server.config.jpa.api.payment.usecase;

import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;
import kr.hhplus.be.server.config.jpa.payment.service.PaymentInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {

	public record Pay (
		Long id,
		Long orderId,
		Long paymentAmount,
		PaymentStatus status
	) {
		public static Pay of(PaymentInfo.Info info) {
			return new Pay(info.getId(), info.getOrderId(), info.getPaymentAmount(), info.getStatus());
		}
	}
}