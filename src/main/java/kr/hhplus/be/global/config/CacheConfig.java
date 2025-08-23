package kr.hhplus.be.global.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.global.config.redis.RedisCache;
import kr.hhplus.be.global.config.redis.TRJackson2JsonRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

		for (RedisCache cache : RedisCache.values()) {
			TRJackson2JsonRedisSerializer<?> serializer = new TRJackson2JsonRedisSerializer<>(objectMapper, cache.getTypeRef());
			RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
				.disableCachingNullValues()
				.prefixCacheNameWith("hhplus:cache:")
				.entryTtl(cache.getExpiredAfterWrite());
			cacheConfigurations.put(cache.getCacheName(), conf);
		}

		return RedisCacheManager.builder(connectionFactory)
			.withInitialCacheConfigurations(cacheConfigurations)
			.build();
	}
}