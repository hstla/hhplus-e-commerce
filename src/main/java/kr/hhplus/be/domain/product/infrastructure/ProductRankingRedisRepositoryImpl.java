package kr.hhplus.be.domain.product.infrastructure;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.product.repository.ProductRankingRedisRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRankingRedisRepositoryImpl implements ProductRankingRedisRepository {

	private final RedisTemplate<String, Long> longRedisTemplate;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");

	@Override
	public void updateProductSalesRanking(Long productId, Integer stock, LocalDateTime now) {
		String todayKey = PRODUCT_TODAY_SALES_RANKING.toKey(now.format(FORMATTER));
		Duration ttl = PRODUCT_TODAY_SALES_RANKING.getTtl();

		longRedisTemplate.opsForZSet().incrementScore(todayKey, productId, stock);

		boolean isNewKey = longRedisTemplate.getExpire(todayKey) < 0;
		if (isNewKey && ttl != null) {
		    longRedisTemplate.expire(todayKey, ttl);
		}
	}

	@Override
	public Set<ZSetOperations.TypedTuple<Long>> get3DaysTopRanking(int topRankLimit) {
		String key = PRODUCT_SALES_RANKING_3DAYS.toKey();
		return longRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, topRankLimit - 1);
	}

	@Override
	public boolean hasKey(String key) {
		return longRedisTemplate.hasKey(key);
	}

	@Override
	public void delete(String top5CacheKey) {
		longRedisTemplate.delete(top5CacheKey);
	}

	@Override
	public void aggregateDailySales(List<String> sourceKeys, String targetKey, Duration ttl) {
		if (sourceKeys.isEmpty()) {
			return;
		}

		ZSetOperations<String, Long> zsetOps = longRedisTemplate.opsForZSet();
		Long result = zsetOps.unionAndStore(sourceKeys.get(0), sourceKeys.subList(1, sourceKeys.size()), targetKey);

		if (result != null && result > 0) {
			longRedisTemplate.expire(targetKey, ttl);
		}
	}
}