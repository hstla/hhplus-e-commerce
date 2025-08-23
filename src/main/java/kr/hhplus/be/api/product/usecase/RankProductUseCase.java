package kr.hhplus.be.api.product.usecase;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import kr.hhplus.be.api.product.controller.dto.ProductResponse;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankProductUseCase {

	private final RedisTemplate<String, Long> redisTemplate;
	private final ProductRepository productRepository;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");
	private static final String DAILY_RANK_KEY_PREFIX = "hhplus:cache:product:sales:";
	private static final String TOP5_CACHE_KEY = "hhplus:cache:product:sales:3days";
	private static final int TOP_RANK_LIMIT = 5;
	private static final int AGGREGATE_DAYS = 3;

	public List<ProductResponse.ProductRank> getTop5Products() {
		// 1) 캐시(정렬된 ZSet)에서 바로 Top‑5 읽기
		Set<ZSetOperations.TypedTuple<Long>> cached = redisTemplate.opsForZSet().reverseRangeWithScores(TOP5_CACHE_KEY, 0, TOP_RANK_LIMIT - 1);

		// 2) 캐시가 비었으면 집계하고 다시 읽는다
		if (cached == null || cached.isEmpty()) {
			log.info("[Rank] TOP‑5 cache miss – aggregating last {} days", AGGREGATE_DAYS);
			aggregateAndCacheTop5();
			cached = redisTemplate.opsForZSet()
				.reverseRangeWithScores(TOP5_CACHE_KEY, 0, TOP_RANK_LIMIT - 1);
		}

		return toRankDto(cached);
	}

	private void aggregateAndCacheTop5() {
		List<String> dailyKeys = new ArrayList<>();
		LocalDate today = LocalDate.now();

		for (int i = 1; i <= AGGREGATE_DAYS; i++) {
			String key = DAILY_RANK_KEY_PREFIX + today.minusDays(i).format(FORMATTER);
			if (redisTemplate.hasKey(key)) {
				dailyKeys.add(key);
				log.debug("[Rank] Found daily data: {}", key);
			} else {
				log.debug("[Rank] No data found for: {}", key);
			}
		}

		if (dailyKeys.isEmpty()) {
			log.warn("[Rank] No daily sales data found for last {} days.");
			redisTemplate.delete(TOP5_CACHE_KEY);
			return;
		}

		ZSetOperations<String, Long> zsetOps = redisTemplate.opsForZSet();

		Long resultCount = zsetOps.unionAndStore(dailyKeys.get(0), dailyKeys.subList(1, dailyKeys.size()), TOP5_CACHE_KEY);

		if (resultCount == null || resultCount == 0) {
			log.warn("[Rank] Aggregation produced empty result – clearing cache");
			redisTemplate.delete(TOP5_CACHE_KEY);
			return;
		}

		redisTemplate.expire(TOP5_CACHE_KEY, Duration.ofDays(1));
		log.info("[Rank] TOP-5 cache refreshed, total aggregated products: {}", resultCount);
	}

	private List<ProductResponse.ProductRank> toRankDto(Set<ZSetOperations.TypedTuple<Long>> raw) {
		List<ProductResponse.ProductRank> result = new ArrayList<>();

		if (raw == null || raw.isEmpty()) {
			return result;
		}

		int rank = 1;
		for (ZSetOperations.TypedTuple<Long> tuple : raw) {
			Long productId = tuple.getValue();
			long soldQty   = tuple.getScore() != null ? tuple.getScore().longValue() : 0L;

			Product product = productRepository.findById(productId);
			ProductResponse.ProductRank dto = ProductResponse.ProductRank.of(productId, product.getName(), product.getDescription(), product.getCategory(), soldQty, rank);

			result.add(dto);
			rank++;
		}
		return result;
	}
}