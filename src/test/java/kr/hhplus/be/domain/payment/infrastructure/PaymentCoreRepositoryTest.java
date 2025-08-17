package kr.hhplus.be.domain.payment.infrastructure;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import kr.hhplus.be.config.RepositoryTestConfig;
import kr.hhplus.be.domain.payment.model.Payment;
import kr.hhplus.be.domain.payment.model.PaymentStatus;

@Import({PaymentCoreRepository.class})
@DisplayName("PaymentCoreRepository 단위 테스트")
class PaymentCoreRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private JpaPaymentRepository jpaPaymentRepository;
	@Autowired
	private PaymentCoreRepository paymentCoreRepository;

	@BeforeEach
	void globalSetUp() {
		jpaPaymentRepository.deleteAll();
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		@Test
		@DisplayName("새로운 Payment 도메인 모델을 성공적으로 저장해야 한다")
		void save_success() {
			// given
			Long orderId = 1L;
			Payment newPayment = Payment.create(orderId, 1_000L);

			// when
			Payment savedPayment = paymentCoreRepository.save(newPayment);

			// then
			assertThat(savedPayment).isNotNull();
			assertThat(savedPayment.getId()).isNotNull();
			assertThat(savedPayment.getOrderId()).isEqualTo(orderId);
			assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
		}
	}
}