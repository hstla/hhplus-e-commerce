package kr.hhplus.be.server.config.jpa.payment.repository;

import kr.hhplus.be.server.config.jpa.payment.model.Payment;

public interface PaymentRepository {
	Payment save(Payment payment);
}
