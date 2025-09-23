package kr.hhplus.be.application.product.usecase;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.repository.ProductRankingRedisRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import kr.hhplus.be.infrastructure.web.controller.product.controller.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankProductUseCase {

	private final ProductRepository productRepository;
	private final ProductRankingRedisRepository productRankingRedisRepository;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");
	private static final int TOP_RANK_LIMIT = 5;
	private static final int AGGREGATE_DAYS = 3;

	public List<ProductResponse.ProductRank> getTop5Products() {
		// 1) 캐시(정렬된 ZSet)에서 바로 Top‑5 읽기
		String topKey = PRODUCT_SALES_RANKING_3DAYS.toKey();
		Set<ZSetOperations.TypedTuple<Long>> topRankCached = productRankingRedisRepository.get3DaysTopRanking(TOP_RANK_LIMIT);

		// 2) 캐시가 비었으면 집계하고 다시 읽는다
		if (topRankCached == null || topRankCached.isEmpty()) {
			aggregateAndCacheTop5(topKey);
			topRankCached = productRankingRedisRepository.get3DaysTopRanking(TOP_RANK_LIMIT);
		}

		return toRankDto(topRankCached);
	}

	private void aggregateAndCacheTop5(String targetKey) {
		List<String> dailyKeys = new ArrayList<>();
		LocalDate today = LocalDate.now();

		for (int i = 1; i <= AGGREGATE_DAYS; i++) {
			String key = PRODUCT_TODAY_SALES_RANKING.toKey(today.minusDays(i).format(FORMATTER));

			if (productRankingRedisRepository.hasKey(key)) {
				dailyKeys.add(key);
			}
		}

		if (dailyKeys.isEmpty()) {
			productRankingRedisRepository.delete(PRODUCT_SALES_RANKING_3DAYS.toKey());
			return;
		}

		productRankingRedisRepository.aggregateDailySales(dailyKeys, targetKey, PRODUCT_SALES_RANKING_3DAYS.getTtl());
	}

	private List<ProductResponse.ProductRank> toRankDto(Set<ZSetOperations.TypedTuple<Long>> raw) {
		List<ProductResponse.ProductRank> result = new ArrayList<>();

		if (raw == null || raw.isEmpty()) {
			return result;
		}

		int rank = 1;
		for (ZSetOperations.TypedTuple<Long> tuple : raw) {
			Long productId = tuple.getValue();
			long soldQty = tuple.getScore() != null ? tuple.getScore().longValue() : 0L;

			Product product = productRepository.findById(productId);
			ProductResponse.ProductRank dto = ProductResponse.ProductRank.of(productId, product.getName(),
				product.getDescription(), product.getCategory(), soldQty, rank);

			result.add(dto);
			rank++;
		}
		return result;
	}
}