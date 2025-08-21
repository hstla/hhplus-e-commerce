package kr.hhplus.be.api.usercoupon.usecase;

import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import kr.hhplus.be.api.usercoupon.usecase.dto.UserCouponCommand;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.coupon.infrastructure.JpaCouponRepository;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.usercoupon.infrastructure.JpaUserCouponRepository;
import kr.hhplus.be.global.config.redis.RedisCacheName;

@DisplayName("CouponPublishIntegrationTest 통합 테스트")
public class CouponPublishIntegrationTest extends IntegrationTestConfig {

	@Autowired
	private QueuePublishCouponUseCase queueCouponUseCase;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private JpaCouponRepository couponRepository;
	@Autowired
	private JpaUserCouponRepository userCouponRepository;
	@Autowired
	private JpaUserRepository userRepository;

	private Coupon testCoupon;
	private final int COUPON_STOCK = 10;
	private final int USER_COUNT = 20; // 상수로 관리하면 더 깔끔합니다.


	@BeforeEach
	void setUp() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
		userCouponRepository.deleteAll();
		couponRepository.deleteAll();
		userRepository.deleteAll();

		List<User> testUsers = LongStream.rangeClosed(1, USER_COUNT)
			.mapToObj(i -> User.create("testuser" + i, "test@email.com" + i, "asdf"+ i))
			.collect(Collectors.toList());
		userRepository.saveAll(testUsers);

		testCoupon = couponRepository.save(new Coupon(null, "Test Coupon", CouponType.FIXED, 1000L, COUPON_STOCK, LocalDateTime.now().plusDays(1)));

		redisTemplate.opsForSet().add(RedisCacheName.VALID_COUPONS, String.valueOf(testCoupon.getId()));

		String stockKey = RedisCacheName.COUPON_STOCK_PREFIX + testCoupon.getId();
		redisTemplate.opsForValue().set(stockKey, String.valueOf(COUPON_STOCK));
	}

	@Test
	@DisplayName("20명이 동시에 쿠폰 발급 요청 시, 재고 10개만큼만 정확히 발급된다.")
	void issueCoupon_concurrent_requests() throws InterruptedException {
		// given
		final ExecutorService executorService = Executors.newFixedThreadPool(USER_COUNT);
		final CountDownLatch latch = new CountDownLatch(USER_COUNT);

		LongStream.rangeClosed(1, USER_COUNT).forEach(userId -> {
			executorService.submit(() -> {
				try {
					queueCouponUseCase.execute(new UserCouponCommand.Publish(userId, testCoupon.getId()));
				} finally {
					latch.countDown();
				}
			});
		});

		latch.await();
		executorService.shutdown();

		Thread.sleep(5000);

		// 3. Assert: 최종 상태 검증
		long issuedCount = userCouponRepository.count();
		// Redis 검증
		String stockKey = RedisCacheName.COUPON_STOCK_PREFIX + testCoupon.getId();
		String stock = redisTemplate.opsForValue().get(stockKey);

		Boolean isCouponValid = redisTemplate.opsForSet().isMember(RedisCacheName.VALID_COUPONS, String.valueOf(testCoupon.getId()));

		String queueKey = RedisCacheName.COUPON_QUEUE_PREFIX + testCoupon.getId();
		Long queueSize = redisTemplate.opsForZSet().size(queueKey);

		Long dbQueueSize = redisTemplate.opsForList().size(RedisCacheName.DB_WRITE_QUEUE);

		assertSoftly(soft -> {
			soft.assertThat(issuedCount).isEqualTo(COUPON_STOCK);
			soft.assertThat(stock).isEqualTo("0");
			soft.assertThat(isCouponValid).isFalse();
			soft.assertThat(queueSize).isZero();
			soft.assertThat(dbQueueSize).isZero();
		});
	}
}