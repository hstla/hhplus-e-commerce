package kr.hhplus.be.api.order.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import kr.hhplus.be.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.global.error.ProductErrorCode;
import lombok.extern.log4j.Log4j2;

@Log4j2
@DisplayName("CreateOrderUseCase 동시성 통합 테스트")
public class CreateOrderUseCaseTestTest extends IntegrationTestConfig {

	@Autowired
	private CreateOrderUseCase createOrderUseCase;
	@Autowired
	private JpaProductOptionRepository productOptionRepository;
	@Autowired
	private JpaProductRepository productRepository;
	@Autowired
	private JpaUserRepository userRepository;
	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	private Long userId;
	private Long productId;
	private ProductOption testOption1;
	private ProductOption testOption2;
	private String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"));

	@BeforeEach
	void setUp() {
		productOptionRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();

		String key = "hhplus:cache:product:rank:" + today;
		redisTemplate.delete(key);

		// 유저 생성
		User user = User.create("testUser", "test@email.com", "password12345");
		userId = userRepository.save(user).getId();

		productId = productRepository.save(Product.create("화장품", ProductCategory.BEAUTY, "설명설명설명설명설명설명설명설명")).getId();

		// 재고 10, 20개짜리 상품 옵션 생성
		testOption1 = ProductOption.create(productId, "테스트 상품1", 1_000L, 10);
		testOption2 = ProductOption.create(productId, "테스트 상품2", 2_000L, 20);
		testOption1 = productOptionRepository.save(testOption1);
		testOption2 = productOptionRepository.save(testOption2);
	}

	@Nested
	@DisplayName("createOrder 성공 케이스")
	class SuccessCase {

		@Test
		@DisplayName("정상 주문 시 재고가 감소해야 한다")
		void createOrder_success() {
			// given
			OrderCommand.Order command = new OrderCommand.Order(
				userId,
				null,
				List.of(new OrderCommand.OrderProduct(testOption1.getId(), 3))
			);

			// when
			OrderResult.Order result = createOrderUseCase.execute(command);

			// then
			ProductOption option = productOptionRepository.findById(testOption1.getId()).orElseThrow();

			assertSoftly(soft -> {
				soft.assertThat(result).isNotNull();
				soft.assertThat(option.getStock()).isEqualTo(7);
			});
		}

		@Test
		@DisplayName("정상 주문 시 Redis에 상품 카운트가 증가해야 한다")
		void createOrder_shouldUpdateRedisRanking() {
			// given
			OrderCommand.Order command = new OrderCommand.Order(
				userId,
				null,
				List.of(new OrderCommand.OrderProduct(testOption1.getId(), 3))
			);

			// when
			createOrderUseCase.execute(command);

			// then
			String key = "hhplus:cache:product:sales:" + today;
			Double score = redisTemplate.opsForZSet().score(key, productId);

			assertSoftly(soft -> {
				soft.assertThat(score).isNotNull();
				soft.assertThat(score).isEqualTo(3);
			});
		}
	}

	@Nested
	@DisplayName("createOrder 실패 및 보상 케이스")
	class FailureAndCompensation {

		@Test
		@DisplayName("재고 부족으로 실패 시 재고가 원복되어야 한다")
		void stockDecreaseFailure_shouldRollback() {
			// given
			OrderCommand.Order command = new OrderCommand.Order(
				userId,
				null,
				List.of(new OrderCommand.OrderProduct(testOption1.getId(), 5), new OrderCommand.OrderProduct(testOption1.getId(), 21))
			);

			// when
			assertThatThrownBy(() -> createOrderUseCase.execute(command)).isInstanceOf(RuntimeException.class);

			// then
			ProductOption option1 = productOptionRepository.findById(testOption1.getId()).orElseThrow();
			ProductOption option2 = productOptionRepository.findById(testOption2.getId()).orElseThrow();

			assertSoftly(soft -> {
				soft.assertThat(option1.getStock()).isEqualTo(10);
				soft.assertThat(option2.getStock()).isEqualTo(20);
			});
		}

		@Test
		@DisplayName("오더/쿠폰 처리 실패 시 재고가 원복되어야 한다")
		void orderTransactionFailure_shouldRollback() {
			// given
			OrderCommand.Order command = new OrderCommand.Order(
				userId,
				999L,
				List.of(OrderCommand.OrderProduct.of(testOption1.getId(), 3), OrderCommand.OrderProduct.of(testOption2.getId(), 15))
			);

			// when
			assertThatThrownBy(() -> createOrderUseCase.execute(command))
				.isInstanceOf(RuntimeException.class);

			// then
			ProductOption option1 = productOptionRepository.findById(testOption1.getId()).orElseThrow();
			ProductOption option2 = productOptionRepository.findById(testOption2.getId()).orElseThrow();
			assertSoftly(soft -> {
				soft.assertThat(option1.getStock()).isEqualTo(10);
				soft.assertThat(option2.getStock()).isEqualTo(20);
			});
		}
	}

	@Nested
	@DisplayName("createOrder 동시성 테스트")
	class ConcurrencyTest {

		@Test
		@DisplayName("여러 스레드가 동시에 주문 시 재고가 정확하게 차감되어야 한다")
		void createOrder_concurrent_success() throws Exception {
			int threadCount = 20;
			int orderQuantityPerThread = 1; // 각 스레드당 주문 수량
			ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
			List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

			List<Callable<Void>> tasks = new ArrayList<>();

			for (int i = 0; i < threadCount; i++) {
				tasks.add(() -> {
					try {
						OrderCommand.Order cmd = new OrderCommand.Order(
							userId,
							null,
							List.of(new OrderCommand.OrderProduct(testOption1.getId(), orderQuantityPerThread))
						);
						createOrderUseCase.execute(cmd);
					} catch (Exception e) {
						exceptions.add(e);
					}
					return null;
				});
			}

			executorService.invokeAll(tasks);
			executorService.shutdown();

			// then
			ProductOption option = productOptionRepository.findById(testOption1.getId()).orElseThrow();
			int failureCount = exceptions.size();
			int successCount = threadCount - failureCount;

			assertSoftly(soft -> {
				soft.assertThat(option.getStock()).isEqualTo(0);
				soft.assertThat(successCount).isEqualTo(10);
				soft.assertThat(failureCount).isEqualTo(10);
				exceptions.forEach(e -> assertThat(e.getMessage()).contains(ProductErrorCode.OUT_OF_STOCK.getMessage()));
			});
		}
	}
}