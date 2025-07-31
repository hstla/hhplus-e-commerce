package kr.hhplus.be.server.config.jpa.payment.service;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.payment.model.Payment;
import kr.hhplus.be.server.config.jpa.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentInfo.Info pay(Long orderId, Long userId, Long paymentAmount) {
		Payment payment = Payment.create(orderId, userId, paymentAmount);
		Payment savePayment = paymentRepository.save(payment);
		return PaymentInfo.Info.of(savePayment);
	}
}
