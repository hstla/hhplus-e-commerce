package kr.hhplus.be.server.config.jpa.api.payment.controller.dto;

import kr.hhplus.be.server.config.jpa.api.payment.usecase.PaymentResult;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponse{

	@Getter
	@NoArgsConstructor
	public static class Payment {
		private Long id;
		private Long orderId;
		private Long userId;
		private Long paymentAmount;
		private PaymentStatus status;

		private Payment(Long id, Long orderId, Long userId, Long paymentAmount, PaymentStatus status) {
			this.id = id;
			this.orderId = orderId;
			this.userId = userId;
			this.paymentAmount = paymentAmount;
			this.status = status;
		}

		public static Payment of(PaymentResult.Pay pay) {
			return new Payment(pay.getId(), pay.getOrderId(), pay.getUserId(), pay.getPaymentAmount(), pay.getStatus());
		}
	}
}