package kr.hhplus.be.server.config.jpa.payment.infrastructure;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.payment.model.Payment;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

	// payment mapper
	public PaymentEntity toEntity(Payment payment) {
		return new PaymentEntity(
			payment.getId(),
			payment.getOrderId(),
			payment.getUserId(),
			payment.getPaymentAmount(),
			payment.getStatus()
		);
	}

	public Payment toModel(PaymentEntity paymentEntity) {
		return new Payment(
			paymentEntity.getId(),
			paymentEntity.getOrderId(),
			paymentEntity.getUserId(),
			paymentEntity.getPaymentAmount(),
			paymentEntity.getStatus()
		);
	}
}
