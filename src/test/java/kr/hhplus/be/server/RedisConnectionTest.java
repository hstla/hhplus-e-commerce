package kr.hhplus.be.server;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RedisConnectionTest {
	@Autowired
	private RedissonClient redissonClient;

	@Test
	void testRedisConnection() {
		// 임시 키에 값 설정 후 읽기
		RBucket<String> bucket = redissonClient.getBucket("test-key");
		bucket.set("hello");
		String value = bucket.get();

		assertThat(value).isEqualTo("hello");
	}
}
