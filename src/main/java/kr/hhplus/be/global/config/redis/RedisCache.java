package kr.hhplus.be.global.config.redis;

import static kr.hhplus.be.global.config.redis.RedisCacheName.*;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import kr.hhplus.be.api.product.query.dto.ProductRankResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisCache {
	PRODUCT_RANK_CACHE(PRODUCT_RANK, Duration.ofDays(1), new TypeReference<List<ProductRankResponse>>() {}),
	;

	private final String cacheName;
	private final Duration expiredAfterWrite;
	private final TypeReference<?> typeRef;
}