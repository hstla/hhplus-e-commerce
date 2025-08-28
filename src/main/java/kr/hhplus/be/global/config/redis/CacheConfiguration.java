package kr.hhplus.be.global.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class CacheConfiguration {

	private final RedisConnectionFactory redisConnectionFactory;
	private final ObjectMapper objectMapper;

	@Bean
	public RedisTemplate<String, Integer> integerRedisTemplate() {
		return createGenericTypeJackson2JsonRedisTemplate(objectMapper, new TypeReference<>() {});
	}

	@Bean
	public RedisTemplate<String, Long> longRedisTemplate() {
		return createGenericTypeJackson2JsonRedisTemplate(objectMapper, new TypeReference<>() {});
	}

	private <V> RedisTemplate<String, V> createGenericTypeJackson2JsonRedisTemplate(
		ObjectMapper objectMapper,
		TypeReference<V> typeRef
	) {
		RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericTypeJackson2JsonRedisSerializer<>(objectMapper, typeRef));
		return redisTemplate;
	}
}