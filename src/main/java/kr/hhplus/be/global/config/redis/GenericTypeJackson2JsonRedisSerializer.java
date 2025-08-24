package kr.hhplus.be.global.config.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericTypeJackson2JsonRedisSerializer<T> implements RedisSerializer<T> {

	private final ObjectMapper objectMapper;
	private final TypeReference<T> typeRef;

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsBytes(t);
		} catch (Exception e) {
			throw new SerializationException("Serialize error", e);
		}
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null) {
			return null;
		}
		try {
			return objectMapper.readValue(bytes, typeRef);
		} catch (Exception e) {
			throw new SerializationException("Could not deserialize object", e);
		}
	}
}