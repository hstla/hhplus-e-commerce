package kr.hhplus.be.domain.product.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;

public interface ProductRankingRedisRepository {

	void updateProductSalesRanking(Long key, Integer value, LocalDateTime now);
	Set<ZSetOperations.TypedTuple<Long>> get3DaysTopRanking(int topRankLimit);
	boolean hasKey(String key);
	void delete(String top5CacheKey);
	void aggregateDailySales(List<String> dailyKeys, String targetKey, Duration ttl);
}
