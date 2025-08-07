package kr.hhplus.be.server.config.jpa.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.config.jpa.payment.model.Payment;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
}