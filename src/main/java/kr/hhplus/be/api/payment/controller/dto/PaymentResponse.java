package kr.hhplus.be.api.payment.controller.dto;

import kr.hhplus.be.api.payment.usecase.PaymentResult;
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