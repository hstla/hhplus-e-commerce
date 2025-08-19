package kr.hhplus.be.api.product.query.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import kr.hhplus.be.api.product.query.QueryTestDataSetUp;
import kr.hhplus.be.api.product.query.dto.ProductRankResponse;
import kr.hhplus.be.api.product.query.repository.ProductRankQueryRepository;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderProductRepository;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.domain.product.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("ProductRankQueryServiceTest 통합 테스트")
class ProductRankQueryServiceTest extends IntegrationTestConfig implements QueryTestDataSetUp {

	@Autowired
	private ProductRankQueryService productRankQueryService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private ProductRankQueryRepository productRankQueryRepository;
	@Autowired
	private JpaProductOptionRepository productOptionRepository;
	@Autowired
	private JpaProductRepository productRepository;
	@Autowired
	private JpaOrderRepository orderRepository;
	@Autowired
	private JpaOrderProductRepository orderProductRepository;

	private Product product1;
	private Product product2;
	private LocalDateTime baseTime;

	@BeforeEach
	void setUp() {
		TestData testData = setUpTestData(productOptionRepository, productRepository, orderRepository, orderProductRepository);
		this.product1 = testData.product1();
		this.product2 = testData.product2();
		this.baseTime = testData.baseTime();
		stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Test
	@DisplayName("getTop5ProductRank 호출 시 캐시 저장 및 재호출 시 캐시에서 조회된다")
	void testCacheableBehavior() {
		// 1. 첫 호출 (DB 조회)
		List<ProductRankResponse> firstCall = productRankQueryService.getTop5ProductRank();
		Assertions.assertThat(firstCall).isNotEmpty();

		// 2. Redis에 키 저장됐는지 확인
		Boolean exists = stringRedisTemplate.hasKey("productRank::top5");
		assertThat(exists).isTrue();
		String json = stringRedisTemplate.opsForValue().get("productRank::top5");
		log.info("JSON: {}", json);

		List<ProductRankResponse> secondCall = productRankQueryService.getTop5ProductRank();
		assertThat(secondCall).isEqualTo(firstCall);
	}
}