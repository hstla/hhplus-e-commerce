package kr.hhplus.be.server.config.jpa.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.payment.model.Payment;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;
import kr.hhplus.be.server.config.jpa.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 단위 테스트")
class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private PaymentService paymentService;

	@Test
	@DisplayName("pay 메서드는 결제 정보를 저장하고 반환한다")
	void pay_success() {
		// given
		Long orderId = 1L;
		Long userId = 2L;
		Long paymentAmount = 3000L;
		Payment savedPayment = new Payment(10L, orderId, userId, paymentAmount, PaymentStatus.COMPLETED);

		given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

		// when
		PaymentInfo.Info result = paymentService.pay(orderId, userId, paymentAmount);

		// then
		then(paymentRepository).should(times(1)).save(any(Payment.class));
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(10L);
		assertThat(result.getOrderId()).isEqualTo(orderId);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getPaymentAmount()).isEqualTo(paymentAmount);
	}
}
