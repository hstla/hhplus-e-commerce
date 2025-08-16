package kr.hhplus.be.server.config.jpa.api.product.query.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.config.jpa.api.product.query.QueryTestDataSetUp;
import kr.hhplus.be.server.config.jpa.api.product.query.dto.ProductRankDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ProductRankQueryServiceTest extends QueryTestDataSetUp {

	@Autowired
	private ProductRankQueryService productRankQueryService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private CacheManager cacheManager;

	@BeforeEach
	void clearCache() {
		stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Test
	@DisplayName("getTop5ProductRank 호출 시 캐시 저장 및 재호출 시 캐시에서 조회된다")
	void testCacheableBehavior() {
		// 1. 첫 호출 (DB 조회)
		List<ProductRankDto> firstCall = productRankQueryService.getTop5ProductRank();
		Assertions.assertThat(firstCall).isNotEmpty();

		// 2. Redis에 키 저장됐는지 확인
		Boolean exists = stringRedisTemplate.hasKey("productRank::top5");
		assertThat(exists).isTrue();
		String json = stringRedisTemplate.opsForValue().get("productRank::top5");
		log.info("JSON: {}", json);

		List<ProductRankDto> secondCall = productRankQueryService.getTop5ProductRank();
		assertThat(secondCall).isEqualTo(firstCall);
	}
}