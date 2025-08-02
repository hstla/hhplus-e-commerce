package kr.hhplus.be.server.config.jpa.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {
}