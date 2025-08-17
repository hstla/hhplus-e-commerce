package kr.hhplus.be.domain.payment.infrastructure;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.payment.model.Payment;
import kr.hhplus.be.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentCoreRepository implements PaymentRepository {

	private final JpaPaymentRepository jpaPaymentRepository;

	@Override
	public Payment save(Payment payment) {
		return jpaPaymentRepository.save(payment);
	}
}