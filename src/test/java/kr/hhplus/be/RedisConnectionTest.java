package kr.hhplus.be;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.config.IntegrationTestConfig;

public class RedisConnectionTest extends IntegrationTestConfig {
	@Autowired
	private RedissonClient redissonClient;

	@Test
	void testRedisConnection() {
		RBucket<String> bucket = redissonClient.getBucket("test-key");
		bucket.set("hello");
		String value = bucket.get();

		assertThat(value).isEqualTo("hello");
	}
}