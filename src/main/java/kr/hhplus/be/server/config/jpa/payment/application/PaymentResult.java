package kr.hhplus.be.server.config.jpa.payment.application;

import kr.hhplus.be.server.config.jpa.payment.domain.PaymentInfo;
import kr.hhplus.be.server.config.jpa.payment.domain.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {

	@Getter
	@NoArgsConstructor
	public static class Pay {
		private Long id;
		private Long orderId;
		private Long userId;
		private Long paymentAmount;
		private PaymentStatus status;

		private Pay(Long id, Long orderId, Long userId, Long paymentAmount, PaymentStatus status) {
			this.id = id;
			this.orderId = orderId;
			this.userId = userId;
			this.paymentAmount = paymentAmount;
			this.status = status;
		}

		public static Pay of(PaymentInfo.Info info) {
			return new Pay(info.getId(), info.getOrderId(), info.getUserId(), info.getPaymentAmount(), info.getStatus());
		}
	}
}