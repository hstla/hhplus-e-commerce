package kr.hhplus.be.infrastructure.persistence.usercoupon;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import kr.hhplus.be.config.RepositoryTestConfig;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.infrastructure.persistence.coupon.JpaCouponRepository;

@Import({UserCouponCoreRepository.class})
@DisplayName("UserCouponCoreRepository 테스트")
class UserCouponCoreRepositoryTest extends RepositoryTestConfig {
	@Autowired
	private JpaUserCouponRepository jpaUserCouponRepository;
	@Autowired
	private JpaCouponRepository jpaCouponRepository;
	@Autowired
	private UserCouponCoreRepository userCouponCoreRepository;

	private Long testCouponId;

	@BeforeEach
	void globalSetUp() {
		jpaUserCouponRepository.deleteAll();
		jpaCouponRepository.deleteAll();

		LocalDateTime expireAt = LocalDateTime.now().plusDays(10);
		Coupon testCouponEntity = Coupon.create("2000원 할인 쿠폰", CouponType.FIXED, 2_000L, 10, expireAt);
		testCouponEntity = jpaCouponRepository.save(testCouponEntity);
		testCouponId = testCouponEntity.getId();
	}

	@Nested
	@DisplayName("findAllByUserId 메서드 테스트")
	class FindAllByUserIdTests {

		private Long userId1 = 10L;
		private Long userId2 = 20L;
		private Long otherCouponId = 0L;

		@BeforeEach
		void setUp() {
			LocalDateTime expireAt = LocalDateTime.now().plusDays(10);
			Coupon testCoupon = Coupon.create("1000원 할인 쿠폰", CouponType.FIXED, 1_000L, 10, expireAt);
			Coupon otherCoupon = jpaCouponRepository.save(testCoupon);
			otherCouponId = otherCoupon.getId();

			// user1에는 2개의 쿠폰 저장
			jpaUserCouponRepository.save(UserCoupon.publish(userId1, testCouponId, LocalDateTime.now().minusDays(1)));
			jpaUserCouponRepository.save(UserCoupon.publish(userId1, otherCouponId, LocalDateTime.now().minusDays(2)));
			// user2에는 1개의 쿠폰 저장
			jpaUserCouponRepository.save(UserCoupon.publish(userId2, testCouponId, LocalDateTime.now().minusDays(3)));
		}

		@Test
		@DisplayName("특정 userId에 해당하는 모든 UserCoupon 도메인 모델을 반환해야 한다")
		void findAllByUserId() {
			// when
			List<UserCoupon> foundUserCoupons = userCouponCoreRepository.findAllByUserId(userId1);

			// then
			assertSoftly(soft -> {
				soft.assertThat(foundUserCoupons).hasSize(2);
				soft.assertThat(foundUserCoupons).extracting(UserCoupon::getUserId).containsOnly(userId1);
				soft.assertThat(foundUserCoupons).extracting(UserCoupon::getCouponId).containsExactlyInAnyOrder(testCouponId, otherCouponId);
			});
		}

		@Test
		@DisplayName("존재하지 않는 userId로 조회 시 빈 리스트를 반환해야 한다")
		void findAllByUserId_empty() {
			// given
			Long nonExistentUserId = 999L;

			// when
			List<UserCoupon> foundUserCoupons = userCouponCoreRepository.findAllByUserId(nonExistentUserId);

			// then
			assertThat(foundUserCoupons).isEmpty();
		}
	}

	@Nested
	@DisplayName("existsByUserIdAndCouponId 메서드 테스트")
	class ExistsByUserIdAndCouponIdTests {

		private Long userId = 30L;

		@BeforeEach
		void setUp() {
			// 테스트용 사용자 쿠폰 저장
			jpaUserCouponRepository.save(UserCoupon.publish(userId, testCouponId, LocalDateTime.now()));
		}

		@Test
		@DisplayName("특정 userId와 couponId 조합의 UserCoupon이 존재하면 true를 반환해야 한다")
		void existsByUserIdAndCouponId() {
			// when
			boolean exists = userCouponCoreRepository.existsByUserIdAndCouponId(userId, testCouponId);

			// then
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("userId는 존재하지만 couponId가 일치하지 않으면 false를 반환해야 한다")
		void existsByUserIdAndCouponId_false_couponId() {
			// given
			Long nonExistentCouponId = 999L;

			// when
			boolean exists = userCouponCoreRepository.existsByUserIdAndCouponId(userId, nonExistentCouponId);

			// then
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("couponId는 존재하지만 userId가 일치하지 않으면 false를 반환해야 한다")
		void existsByUserIdAndCouponId_fail_userId() {
			// given
			Long nonExistentUserId = 888L;

			// when
			boolean exists = userCouponCoreRepository.existsByUserIdAndCouponId(nonExistentUserId, testCouponId);

			// then
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("userId와 couponId 모두 존재하지 않으면 false를 반환해야 한다")
		void existsByUserIdAndCouponId_fail_userId_couponId() {
			// given
			Long nonExistentUserId = 888L;
			Long nonExistentCouponId = 999L;

			// when
			boolean exists = userCouponCoreRepository.existsByUserIdAndCouponId(nonExistentUserId, nonExistentCouponId);

			// then
			assertThat(exists).isFalse();
		}
	}
}
