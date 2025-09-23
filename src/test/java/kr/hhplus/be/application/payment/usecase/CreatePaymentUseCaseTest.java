package kr.hhplus.be.application.payment.usecase;

import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.config.ConcurrentTestSupport;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.payment.model.PaymentType;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.infrastructure.persistence.order.JpaOrderRepository;
import kr.hhplus.be.infrastructure.persistence.payment.JpaPaymentRepository;
import kr.hhplus.be.infrastructure.persistence.user.JpaUserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("CreatePaymentUseCase 동시성 통합 테스트")
class CreatePaymentUseCaseTest extends ConcurrentTestSupport {

	@Autowired
	private CreatePaymentUseCase createPaymentUseCase;
	@Autowired
	private JpaUserRepository userRepository;
	@Autowired
	private JpaOrderRepository orderRepository;
	@Autowired
	private JpaPaymentRepository paymentRepository;

	private long userId;
	private final long orderPrice = 20_000L;
	private final int numberOfThreads = 10;

	@BeforeEach
	void setUp() {
		paymentRepository.deleteAll();
		orderRepository.deleteAll();
		userRepository.deleteAll();

		User user = User.create("name", "test@email.com", "password");
		user.chargePoint(20_000L);
		User savedUser = userRepository.save(user);
		userId = savedUser.getId();

		IntStream.range(0, numberOfThreads)
			.forEach(i -> orderRepository.save(
				Order.create(userId, null, orderPrice, 0L, orderPrice, LocalDateTime.now())
			));
	}

	@AfterEach
	void tearDown() {
		paymentRepository.deleteAll();
		orderRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Nested
	@DisplayName("payment 동시성 테스트")
	class Payment {

		@Test
		@DisplayName("한명의 유저가 서로 다른 주문을 동시에 주문하면 하나만 성공하고 나머지는 실패한다.")
		void pay_success_and_fail_when_concurrently() throws InterruptedException {
			// given
			List<Long> orderIds = orderRepository.findByUserId(userId)
				.stream()
				.map(Order::getId)
				.toList();

			// when
			ConcurrentTestResult<Void> result = runConcurrentTestWithIndex(
				numberOfThreads,
				(threadIndex) -> {
					Long orderId = orderIds.get(threadIndex);
					PaymentCommand.Pay command = PaymentCommand.Pay.of(orderId, userId, PaymentType.POINT);
					createPaymentUseCase.execute(command);
					return null;
				}
			);

			// then
			long paymentCount = paymentRepository.count();
			User userAfterPayment = userRepository.findById(userId).get();

			assertSoftly(soft -> {
				soft.assertThat(paymentCount).isEqualTo(1);
				soft.assertThat(userAfterPayment.getPoint().getAmount()).isEqualTo(0L);
				soft.assertThat(result.exceptions()).hasSize(numberOfThreads - 1);
				soft.assertThat(result.exceptions().get(0)).isInstanceOf(RestApiException.class);
				soft.assertThat(result.exceptions().get(0).getMessage()).isEqualTo("Insufficient user points");
			});
		}
	}

	@Test
	@DisplayName("한명의 유저가 하나의 주문을 동시에 요청하면 하나만 성공하고 나머지는 실패한다.")
	void pay_same_order_concurrently() throws InterruptedException {
		// given
		Order order = orderRepository.save(
			Order.create(userId, null, orderPrice, 0L, orderPrice, LocalDateTime.now())
		);

		// when
		ConcurrentTestResult<Void> result = runConcurrentTest(
			numberOfThreads,
			() -> {
				PaymentCommand.Pay command = PaymentCommand.Pay.of(order.getId(), userId, PaymentType.POINT);
				createPaymentUseCase.execute(command);
				return null;
			});

		// then
		long paymentCount = paymentRepository.count();
		User userAfterPayment = userRepository.findById(userId).get();

		assertSoftly(soft -> {
			soft.assertThat(paymentCount).isEqualTo(1);
			soft.assertThat(userAfterPayment.getPoint().getAmount()).isEqualTo(0L);
			soft.assertThat(result.exceptions()).hasSize(numberOfThreads - 1);
			soft.assertThat(result.exceptions().get(0).getMessage()).isEqualTo("Insufficient user points");
		});
	}
}