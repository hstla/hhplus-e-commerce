package kr.hhplus.be.global.config.redis;

import java.time.Duration;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisCache {
	;

	private final String cacheName;
	private final Duration expiredAfterWrite;
	private final TypeReference<?> typeRef;
}