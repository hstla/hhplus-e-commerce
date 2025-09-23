package kr.hhplus.be.application.usercoupon.usecase;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import kr.hhplus.be.application.usercoupon.dto.UserCouponCommand;
import kr.hhplus.be.config.ConcurrentTestSupport;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.model.UserCouponStatus;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.infrastructure.persistence.coupon.JpaCouponRepository;
import kr.hhplus.be.infrastructure.persistence.user.JpaUserRepository;
import kr.hhplus.be.infrastructure.persistence.usercoupon.JpaUserCouponRepository;

@DisplayName("실시간 쿠폰 발급 end to end 테스트")
public class IssueCouponE2ETest extends ConcurrentTestSupport {

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

	private Coupon testCoupon1;
	private Coupon testCoupon2;
	private User testUser;
	private final int COUPON1_STOCK = 10;
	private final int COUPON2_STOCK = 1;


	@BeforeEach
	void setUp() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
		userCouponRepository.deleteAll();
		couponRepository.deleteAll();
		userRepository.deleteAll();

		testUser = User.create("testuser", "test@email.com", "asdf1");
		userRepository.save(testUser);

		testCoupon1 = Coupon.create("1000원 쿠폰", CouponType.FIXED, 1000L, COUPON1_STOCK,
			LocalDateTime.now().plusDays(1));
		testCoupon2 = Coupon.create("2000원 쿠폰", CouponType.FIXED, 2000L, COUPON2_STOCK,
			LocalDateTime.now().plusDays(1));

		couponRepository.saveAll(List.of(testCoupon1, testCoupon2));
		redisTemplate.opsForSet().add(COUPON_VALID_SET.toKey(), String.valueOf(this.testCoupon1.getId()));
		redisTemplate.opsForSet().add(COUPON_VALID_SET.toKey(), String.valueOf(this.testCoupon2.getId()));

		String stockKey1 = COUPON_STOCK_CACHE.toKey(testCoupon1.getId());
		String stockKey2 = COUPON_STOCK_CACHE.toKey(testCoupon2.getId());
		redisTemplate.opsForValue().set(stockKey1, String.valueOf(COUPON1_STOCK));
		redisTemplate.opsForValue().set(stockKey2, String.valueOf(COUPON2_STOCK));
	}

	@Nested
	@DisplayName("정상 발급 케이스")
	class SuccessCase {

		@Test
		@DisplayName("사용자가 쿠폰을 정상적으로 발급받는다")
		void issueCouponSuccessfully() {
			// given
			UserCouponCommand.Publish publish = UserCouponCommand.Publish.of(testUser.getId(), testCoupon1.getId());

			// when
			issueCouponUseCase.execute(publish);

			// then
			await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
				List<UserCoupon> issuedCoupons = userCouponRepository.findAllByUserId(testUser.getId());
				assertThat(issuedCoupons).hasSize(1);
				assertSoftly(soft -> {
					soft.assertThat(issuedCoupons).hasSize(1);
					UserCoupon userCoupon = issuedCoupons.get(0);

					soft.assertThat(userCoupon.getUserId()).isEqualTo(testUser.getId());
					soft.assertThat(userCoupon.getCouponId()).isEqualTo(testCoupon1.getId());
					soft.assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
					soft.assertThat(userCoupon.getIssuedAt()).isNotNull();
					soft.assertThat(userCoupon.getUsedAt()).isNull();
				});
			});
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class FailureCase {

		@Test
		@DisplayName("같은 사용자가 연속 요청 시 발급에 실패한다")
		void failDueToDuplicateRequest() {
			// given
			UserCouponCommand.Publish publish = UserCouponCommand.Publish.of(testUser.getId(), testCoupon2.getId());

			// when
			issueCouponUseCase.execute(publish);

			// then
			assertThatThrownBy(() -> issueCouponUseCase.execute(publish))
				.isInstanceOf(RestApiException.class)
				.hasMessageContaining(CouponErrorCode.TOO_MANY_REQUESTS.getMessage());
		}

		@Test
		@DisplayName("쿠폰 재고가 없으면 발급에 실패한다")
		void failDueToOutOfStock() {
			// given
			User otherUser = User.create("otheruser", "other@email.com", "pass2");
			userRepository.save(otherUser);

			UserCouponCommand.Publish first = UserCouponCommand.Publish.of(testUser.getId(), testCoupon2.getId());
			UserCouponCommand.Publish second = UserCouponCommand.Publish.of(otherUser.getId(), testCoupon2.getId());

			// when
			issueCouponUseCase.execute(first);

			// then
			await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
				assertThatThrownBy(() -> issueCouponUseCase.execute(second))
					.isInstanceOf(RestApiException.class)
					.hasMessage(CouponErrorCode.INACTIVE_COUPON.getMessage());
			});
		}
	}

	@Nested
	@DisplayName("동시성 케이스")
	class ConcurrencyCase {

		@Test
		@DisplayName("마지막 한 개 남은 쿠폰을 여러 사용자가 요청하면 한 명만 발급받는다.")
		void lastStockConcurrency() throws InterruptedException {
			// given
			int numberOfThreads = 10;

			// when
			runConcurrentTestWithIndex(
				numberOfThreads
				, index -> {
				User user = userRepository.save(User.create("user-" + index, "test@test.com" + index, "password" + index));
				UserCouponCommand.Publish publish = UserCouponCommand.Publish.of(user.getId(), testCoupon2.getId());
				issueCouponUseCase.execute(publish);
				return null;
			});

			// then
			await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
				List<UserCoupon> issuedCoupons = userCouponRepository.findAllByCouponId(testCoupon2.getId());

				assertSoftly(soft -> {
					soft.assertThat(issuedCoupons).hasSize(1); // 단 1명만 발급
				});
			});
		}
	}
}