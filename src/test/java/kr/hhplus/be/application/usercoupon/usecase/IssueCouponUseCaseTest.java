package kr.hhplus.be.application.usercoupon.usecase;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import kr.hhplus.be.application.usercoupon.dto.UserCouponCommand;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.infrastructure.persistence.coupon.JpaCouponRepository;
import kr.hhplus.be.infrastructure.persistence.user.JpaUserRepository;
import kr.hhplus.be.infrastructure.persistence.usercoupon.JpaUserCouponRepository;

@DisplayName("IssueCouponUseCaseTest 통합 테스트")
public class IssueCouponUseCaseTest extends IntegrationTestConfig {

	@Autowired
	private IssueCouponUseCase issueCouponUseCase;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private JpaCouponRepository couponRepository;
	@Autowired
	private JpaUserCouponRepository userCouponRepository;
	@Autowired
	private JpaUserRepository userRepository;

	private Coupon testCoupon;
	private User testUser;
	private final int COUPON_STOCK = 10;


	@BeforeEach
	void setUp() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
		userCouponRepository.deleteAll();
		couponRepository.deleteAll();
		userRepository.deleteAll();

		testUser = User.create("testuser", "test@email.com", "asdf1");
		userRepository.save(testUser);

		testCoupon = couponRepository.save(new Coupon(null, "Test Coupon", CouponType.FIXED, 1000L, COUPON_STOCK, LocalDateTime.now().plusDays(1)));
		redisTemplate.opsForSet().add(COUPON_VALID_SET.toKey(), String.valueOf(testCoupon.getId()));

		String stockKey = COUPON_STOCK_CACHE.toKey(testCoupon.getId());
		redisTemplate.opsForValue().set(stockKey, String.valueOf(COUPON_STOCK));
	}

	@Test
	@DisplayName("쿠폰 발급에 성공하면 taskId를 반환한다.")
	void issueCoupon_success() {
		// given
		UserCouponCommand.Publish execute = UserCouponCommand.Publish.of(testUser.getId(), testCoupon.getId());

		// when
		String taskId = issueCouponUseCase.execute(execute);

		// then
		Boolean isCouponValid = redisTemplate.opsForSet().isMember(COUPON_VALID_SET.toKey(), String.valueOf(testCoupon.getId()));

		assertSoftly(soft -> {
			soft.assertThat(isCouponValid).isTrue();
			soft.assertThat(taskId).isNotNull();
		});
	}
}