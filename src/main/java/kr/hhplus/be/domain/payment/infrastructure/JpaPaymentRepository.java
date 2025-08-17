package kr.hhplus.be.domain.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.payment.model.Payment;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
}