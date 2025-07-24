package kr.hhplus.be.server.config.jpa.payment.domain;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentInfo.Info pay(Long orderId, Long userId, Long paymentAmount) {
		Payment payment = Payment.createPayment(orderId, userId, paymentAmount);
		Payment savePayment = paymentRepository.save(payment);
		return PaymentInfo.Info.of(savePayment);
	}
}
