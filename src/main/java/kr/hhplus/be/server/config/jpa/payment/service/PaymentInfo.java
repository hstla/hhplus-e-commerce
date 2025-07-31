package kr.hhplus.be.server.config.jpa.payment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentInfo {

	@Getter
	@NoArgsConstructor
	public static class Info {
		private Long id;
		private Long orderId;
		private Long userId;
		private Long paymentAmount;
		private PaymentStatus status;

		private Info(Long id, Long orderId, Long userId, Long paymentAmount, PaymentStatus status) {
			this.id = id;
			this.orderId = orderId;
			this.userId = userId;
			this.paymentAmount = paymentAmount;
			this.status = status;
		}

		public static Info of(Payment payment) {
			return new Info(payment.getId(), payment.getOrderId(), payment.getUserId(), payment.getPaymentAmount(), payment.getStatus());
		}
	}
}
