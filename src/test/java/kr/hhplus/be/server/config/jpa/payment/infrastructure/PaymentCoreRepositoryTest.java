package kr.hhplus.be.server.config.jpa.payment.infrastructure;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.TestcontainersConfig;
import kr.hhplus.be.server.config.jpa.payment.model.Payment;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PaymentCoreRepository.class, PaymentMapper.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("PaymentCoreRepository 단위 테스트")
class PaymentCoreRepositoryTest {

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
			Long userId = 1L;
			Payment newPayment = Payment.create(orderId, userId, 1_000L);

			// when
			Payment savedPayment = paymentCoreRepository.save(newPayment);

			// then
			assertThat(savedPayment).isNotNull();
			assertThat(savedPayment.getId()).isNotNull();
			assertThat(savedPayment.getOrderId()).isEqualTo(orderId);
			assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
		}

		@Test
		@DisplayName("기존 Payment 도메인 모델의 상태를 성공적으로 업데이트해야 한다")
		void save_update() {
			// given
			Long orderId = 2L;
			Long userId = 1L;
			PaymentEntity existingEntity = new PaymentEntity(orderId, userId, 20_000L, PaymentStatus.PENDING);
			PaymentEntity savedOriginalEntity = jpaPaymentRepository.save(existingEntity);

			Payment updatePayment = new Payment(savedOriginalEntity.getId(), orderId, userId, 10_000L, PaymentStatus.COMPLETED);

			// when
			Payment updatedPayment = paymentCoreRepository.save(updatePayment);

			// then
			assertThat(updatedPayment.getId()).isEqualTo(savedOriginalEntity.getId());
			assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
			assertThat(updatedPayment.getPaymentAmount()).isEqualTo(10_000L);
		}
	}
}