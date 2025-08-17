package kr.hhplus.be.domain.payment.repository;

import kr.hhplus.be.domain.payment.model.Payment;

public interface PaymentRepository {
	Payment save(Payment payment);
}