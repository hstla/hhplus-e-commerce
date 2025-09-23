package kr.hhplus.be.infrastructure.web.controller.payment.controller.dto;

import kr.hhplus.be.application.payment.dto.PaymentResult;
import kr.hhplus.be.domain.payment.model.PaymentStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponse{

	public record Payment (
		Long id,
		Long orderId,
		Long paymentAmount,
		PaymentStatus status
	) {
		public static Payment of(PaymentResult.Pay pay) {
			return new Payment(pay.id(), pay.orderId(), pay.paymentAmount(), pay.status());
		}
	}
}