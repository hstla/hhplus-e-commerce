package kr.hhplus.be.api.product.usecase;

import static org.assertj.core.api.SoftAssertions.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import kr.hhplus.be.api.product.controller.dto.ProductResponse;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("RankProductUseCase 통합 테스트")
class RankProductUseCaseTest extends IntegrationTestConfig {

	@Autowired
	private RankProductUseCase rankProductUseCase;
	@Autowired
	private JpaUserRepository userRepository;
	@Autowired
	private JpaProductRepository productRepository;
	@Autowired
	private JpaProductOptionRepository productOptionRepository;
	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	private Long userId;
	private Long productId1;
	private Long productId2;
	private Long productId3;
	private Long productId4;
	private Long productId5;
	private Long productId6;

	@BeforeEach
	void setUp() {
		// 테스트를 위해 DB와 Redis 초기화
		userRepository.deleteAll();
		productOptionRepository.deleteAll();
		productRepository.deleteAll();
		redisTemplate.getConnectionFactory().getConnection().flushAll();

		// 테스트용 유저 및 상품 생성
		User user = User.create("testUser", "test@email.com", "password12345");
		userId = userRepository.save(user).getId();

		productId1 = productRepository.save(Product.create("아이폰", ProductCategory.DIGITAL, "설명설명설명설명설명")).getId();
		productId2 = productRepository.save(Product.create("갤럭시", ProductCategory.DIGITAL, "설명설명설명설명설명")).getId();
		productId3 = productRepository.save(Product.create("맥북", ProductCategory.DIGITAL, "설명설명설명설명설명")).getId();
		productId4 = productRepository.save(Product.create("아이패드", ProductCategory.DIGITAL, "설명설명설명설명설명")).getId();
		productId5 = productRepository.save(Product.create("에어팟", ProductCategory.DIGITAL, "설명설명설명설명설명")).getId();
		productId6 = productRepository.save(Product.create("애플워치", ProductCategory.DIGITAL, "설명설명설명설명설명")).getId();

		productOptionRepository.save(ProductOption.create(productId1, "옵션1", 1000L, 100));
		productOptionRepository.save(ProductOption.create(productId2, "옵션2", 2000L, 100));
		productOptionRepository.save(ProductOption.create(productId3, "옵션3", 3000L, 100));
		productOptionRepository.save(ProductOption.create(productId4, "옵션4", 4000L, 100));
		productOptionRepository.save(ProductOption.create(productId5, "옵션5", 5000L, 100));
		productOptionRepository.save(ProductOption.create(productId6, "옵션6", 6000L, 100));
	}

	@Nested
	@DisplayName("getTop5Products 성공 케이스")
	class SuccessCases {

		@Test
		@DisplayName("캐시 미스 시 3일간의 데이터를 집계하여 Top 5를 생성한다")
		void getTop5Products_shouldAggregateAndCache() {
			// given
			// 어제 날짜에 판매 데이터 생성: product1(10), product2(5)
			String todayKey = "hhplus:cache:product:sales:" + LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yy-MM-dd"));
			redisTemplate.opsForZSet().add(todayKey, productId1, 10);
			redisTemplate.opsForZSet().add(todayKey, productId2, 5);
			redisTemplate.expire(todayKey, Duration.ofDays(3));

			// 그제 날짜에 판매 데이터 생성: product3(8), product4(7)
			String yesterdayKey = "hhplus:cache:product:sales:" + LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yy-MM-dd"));
			redisTemplate.opsForZSet().add(yesterdayKey, productId3, 8);
			redisTemplate.opsForZSet().add(yesterdayKey, productId4, 7);
			redisTemplate.expire(yesterdayKey, Duration.ofDays(3));

			// 그끄제 날짜에 판매 데이터 생성: product5(4), product6(9), product2(1)
			String dayBeforeYesterdayKey = "hhplus:cache:product:sales:" + LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("yy-MM-dd"));
			redisTemplate.opsForZSet().add(dayBeforeYesterdayKey, productId5, 4);
			redisTemplate.opsForZSet().add(dayBeforeYesterdayKey, productId6, 9);
			redisTemplate.opsForZSet().add(dayBeforeYesterdayKey, productId2, 1);
			redisTemplate.expire(dayBeforeYesterdayKey, Duration.ofDays(3));

			// when
			List<ProductResponse.ProductRank> topProducts = rankProductUseCase.getTop5Products();

			// then
			assertSoftly(soft -> {
				// 반환된 리스트 크기 검증
				soft.assertThat(topProducts).hasSize(5);

				// 랭킹과 판매 수량 검증
				soft.assertThat(topProducts.get(0).productId()).isEqualTo(productId1);
				soft.assertThat(topProducts.get(0).totalSold()).isEqualTo(10);
				soft.assertThat(topProducts.get(0).rank()).isEqualTo(1);

				soft.assertThat(topProducts.get(1).productId()).isEqualTo(productId6);
				soft.assertThat(topProducts.get(1).totalSold()).isEqualTo(9);
				soft.assertThat(topProducts.get(1).rank()).isEqualTo(2);

				soft.assertThat(topProducts.get(2).productId()).isEqualTo(productId3);
				soft.assertThat(topProducts.get(2).totalSold()).isEqualTo(8);
				soft.assertThat(topProducts.get(2).rank()).isEqualTo(3);

				soft.assertThat(topProducts.get(3).productId()).isEqualTo(productId4);
				soft.assertThat(topProducts.get(3).totalSold()).isEqualTo(7);
				soft.assertThat(topProducts.get(3).rank()).isEqualTo(4);

				soft.assertThat(topProducts.get(4).productId()).isEqualTo(productId2);
				soft.assertThat(topProducts.get(4).totalSold()).isEqualTo(6); // 5 + 1
				soft.assertThat(topProducts.get(4).rank()).isEqualTo(5);

				// 캐시가 올바르게 생성되었는지 확인
				soft.assertThat(redisTemplate.hasKey("hhplus:cache:product:sales:3days")).isTrue();
			});
		}

		@Test
		@DisplayName("캐시 히트 시 집계 로직을 건너뛰고 바로 캐시에서 데이터를 읽어온다")
		void getTop5Products_shouldReadFromCache() {
			// given
			// 캐시를 미리 생성
			String cacheKey = "hhplus:cache:product:sales:3days";
			redisTemplate.opsForZSet().add(cacheKey, productId1, 100);
			redisTemplate.opsForZSet().add(cacheKey, productId2, 90);
			redisTemplate.opsForZSet().add(cacheKey, productId3, 80);
			redisTemplate.opsForZSet().add(cacheKey, productId4, 70);
			redisTemplate.opsForZSet().add(cacheKey, productId5, 60);

			// when
			List<ProductResponse.ProductRank> topProducts = rankProductUseCase.getTop5Products();

			// then
			assertSoftly(softly -> {
				softly.assertThat(topProducts).hasSize(5);
				softly.assertThat(topProducts.get(0).productId()).isEqualTo(productId1);
				softly.assertThat(topProducts.get(0).totalSold()).isEqualTo(100);
			});
		}

		@Test
		@DisplayName("판매 데이터가 5개 미만일 경우, 있는 데이터만 반환한다")
		void getTop5Products_shouldReturnFewerThan5() {
			// given
			// 판매 데이터 2개만 생성
			String todayKey = "hhplus:cache:product:sales:" + LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yy-MM-dd"));
			redisTemplate.opsForZSet().add(todayKey, productId1, 10);
			redisTemplate.opsForZSet().add(todayKey, productId2, 5);
			redisTemplate.expire(todayKey, Duration.ofDays(3));

			// when
			List<ProductResponse.ProductRank> topProducts = rankProductUseCase.getTop5Products();

			// then
			assertSoftly(soft -> {
				soft.assertThat(topProducts).hasSize(2);
				soft.assertThat(topProducts.get(0).productId()).isEqualTo(productId1);
				soft.assertThat(topProducts.get(1).productId()).isEqualTo(productId2);
			});
		}
	}

	@Nested
	@DisplayName("getTop5Products 실패 케이스")
	class FailureCases {

		@Test
		@DisplayName("Redis에 판매 데이터가 전혀 없을 경우, 빈 목록을 반환한다")
		void getTop5Products_shouldReturnEmptyListWhenNoData() {
			// given Redis에 아무런 데이터가 없는 상태

			// when
			List<ProductResponse.ProductRank> topProducts = rankProductUseCase.getTop5Products();

			// then
			assertSoftly(soft -> {
				soft.assertThat(topProducts).isEmpty();
				soft.assertThat(redisTemplate.hasKey("hhplus:cache:product:sales:3days")).isFalse();
			});
		}
	}
}