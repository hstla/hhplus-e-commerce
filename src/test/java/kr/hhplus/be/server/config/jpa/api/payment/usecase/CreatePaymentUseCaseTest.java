package kr.hhplus.be.server.config.jpa.api.payment.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.payment.infrastructure.JpaPaymentRepository;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentType;
import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CreatePaymentUseCase 동시성 통합 테스트")
class CreatePaymentUseCaseTest {

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
	private final int numberOfThreads = 4;

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
		@DisplayName("2개의 주문을 동시에 결제하면 1개는 성공, 1개는 실패하며 포인트는 0이 되어야 한다")
		void pay_success_and_fail_when_concurrently() throws InterruptedException {
			// given
			List<Long> orderIds = orderRepository.findByUserId(userId)
				.stream()
				.map(Order::getId)
				.toList();

			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch latch = new CountDownLatch(numberOfThreads);

			List<Throwable> exceptions = new ArrayList<>();

			// when
			for (Long orderId : orderIds) {
				executorService.submit(() -> {
					try {
						PaymentCommand.Pay command = PaymentCommand.Pay.of(orderId, userId, PaymentType.POINT);
						createPaymentUseCase.execute(command);
					} catch (Exception e) {
						synchronized (exceptions) {
							exceptions.add(e);
						}
					} finally {
						latch.countDown();
					}
				});
			}

			latch.await();
			executorService.shutdown();

			// then
			long paymentCount = paymentRepository.count();
			User userAfterPayment = userRepository.findById(userId).get();
			assertAll(
				() -> assertThat(paymentCount).isEqualTo(1),
				() -> assertThat(userAfterPayment.getPoint().getAmount()).isEqualTo(0L),
				() -> assertThat(exceptions).hasSize(numberOfThreads - 1),
				() -> assertThat(exceptions.get(0)).isInstanceOf(RestApiException.class),
				() -> assertThat(exceptions.get(0).getMessage()).isEqualTo(UserErrorCode.INSUFFICIENT_USER_POINT.getMessage())
			);
		}
	}
}