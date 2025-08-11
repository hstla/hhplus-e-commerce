package kr.hhplus.be.server.config.jpa.api.order.usecase;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderProductRepository;
import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.server.config.jpa.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.server.config.jpa.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CreateOrderUseCase 동시성 통합 테스트")
class CreateOrderUseCaseTest {

	@Autowired
	private CreateOrderUseCase createOrderUseCase;
	@Autowired
	private JpaUserRepository userRepository;
	@Autowired
	private JpaProductOptionRepository productOptionRepository;
	@Autowired
	private JpaProductRepository productRepository;
	@Autowired
	private JpaOrderRepository orderRepository;
	@Autowired
	private JpaOrderProductRepository orderProductRepository;

	private Long userId;
	private Long productOptionId;
	private final int initialStock = 2;
	private final int orderQuantity = 2;
	private final int numberOfThreads = 2;
	private final int expectedSuccesses = initialStock / orderQuantity;

	private void executeConcurrency(int threadCount, Runnable runnable) {
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		IntStream.range(0, threadCount).forEach(i ->
			executorService.submit(() -> {
				try {
					runnable.run();
				} finally {
					latch.countDown();
				}
			})
		);

		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		executorService.shutdown();
	}

	@BeforeEach
	void setUp() {
		// DB 초기화
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		productOptionRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();

		// 테스트 데이터 생성
		User user = User.create("testUser", "test@test.com", "password");
		userId = userRepository.save(user).getId();

		Product product = Product.create("Test Product", ProductCategory.FOOD, "test description");
		Product savedProduct = productRepository.save(product);

		ProductOption option = ProductOption.create(savedProduct.getId(), "meat", 20_000L, initialStock);
		productOptionId = productOptionRepository.save(option).getId();
	}

	@AfterEach
	void tearDown() {
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		productOptionRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Nested
	@DisplayName("createOrder 동시성 테스트")
	class CreateOrder {

		@Test
		@DisplayName("여러 스레드가 동시에 주문 시 재고가 정확하게 차감되어야 한다")
		void createOrder_concurrent_success() {
			// given
			AtomicInteger successCount = new AtomicInteger();
			AtomicInteger failCount = new AtomicInteger();

			// when
			executeConcurrency(numberOfThreads, () -> {
				try {
					OrderCommand.OrderProduct orderProduct = new OrderCommand.OrderProduct(productOptionId, orderQuantity);
					OrderCommand.Order command = new OrderCommand.Order(userId, null, List.of(orderProduct));
					createOrderUseCase.execute(command);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				}
			});

			// then
			assertThat(successCount.get()).isEqualTo(expectedSuccesses);
			assertThat(failCount.get()).isEqualTo(numberOfThreads - expectedSuccesses);

			ProductOption finalOption = productOptionRepository.findById(productOptionId).get();
			assertThat(finalOption.getStock()).isEqualTo(0);
		}
	}
}
