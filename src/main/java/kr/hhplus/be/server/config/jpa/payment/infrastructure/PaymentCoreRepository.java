package kr.hhplus.be.server.config.jpa.payment.infrastructure;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.payment.model.Payment;
import kr.hhplus.be.server.config.jpa.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentCoreRepository implements PaymentRepository {

	private final JpaPaymentRepository jpaPaymentRepository;
	private final PaymentMapper paymentMapper;

	@Override
	public Payment save(Payment payment) {
		PaymentEntity save = jpaPaymentRepository.save(paymentMapper.toEntity(payment));
		return paymentMapper.toModel(save);
	}
}