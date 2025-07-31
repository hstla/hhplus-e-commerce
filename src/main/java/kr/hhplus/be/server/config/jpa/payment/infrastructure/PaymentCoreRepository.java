package kr.hhplus.be.server.config.jpa.payment.infrastructure;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.payment.domain.Payment;
import kr.hhplus.be.server.config.jpa.payment.domain.PaymentRepository;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

	@Override
	public Payment save(Payment payment) {
		return null;
	}
}