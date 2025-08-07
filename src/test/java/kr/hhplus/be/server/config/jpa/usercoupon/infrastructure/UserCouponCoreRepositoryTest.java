package kr.hhplus.be.server.config.jpa.coupon.infrastructure.usercoupon;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.TestcontainersConfig;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.JpaCouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.mapper.CouponMapper;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.infrastructure.JpaUserCouponRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCouponStatus;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({UserCouponCoreRepository.class, CouponMapper.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("UserCouponCoreRepository 테스트")
class UserCouponCoreRepositoryTest {
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
		CouponEntity testCouponEntity = new CouponEntity(CouponType.FIXED, "2000원 할인 쿠폰", 2_000L, 10, expireAt);
		testCouponEntity = jpaCouponRepository.save(testCouponEntity);
		testCouponId = testCouponEntity.getId();
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		@Test
		@DisplayName("새로운 UserCoupon 도메인 모델을 성공적으로 저장해야 한다")
		void save() {
			// given
			Long userId = 1L;
			LocalDateTime issuedAt = LocalDateTime.now();
			UserCoupon newUserCoupon = UserCoupon.publish(userId, testCouponId, issuedAt);

			// when
			UserCoupon savedUserCoupon = userCouponCoreRepository.save(newUserCoupon);

			// then
			assertThat(savedUserCoupon.getId()).isNotNull();
			assertThat(savedUserCoupon.getUserId()).isEqualTo(userId);
			assertThat(savedUserCoupon.getCouponId()).isEqualTo(testCouponId);
			assertThat(savedUserCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
			assertThat(savedUserCoupon.getIssuedAt()).isEqualTo(issuedAt);
			assertThat(savedUserCoupon.getUsedAt()).isNull();
		}

		@Test
		@DisplayName("기존 UserCoupon 도메인 모델의 상태를 성공적으로 업데이트해야 한다")
		void save_update() {
			// given
			Long userId = 2L;
			LocalDateTime issuedAt = LocalDateTime.now().minusDays(5);
			UserCouponEntity existingEntity = new UserCouponEntity(
				userId, testCouponId, UserCouponStatus.ISSUED, issuedAt, null
			);
			UserCouponEntity savedOriginalEntity = jpaUserCouponRepository.save(existingEntity);

			UserCoupon userCouponToUpdate = userCouponCoreRepository.findById(savedOriginalEntity.getId());
			LocalDateTime usedAt = LocalDateTime.now();
			userCouponToUpdate.use(usedAt);

			// when
			UserCoupon updatedUserCoupon = userCouponCoreRepository.save(userCouponToUpdate);

			// then
			assertThat(updatedUserCoupon).isNotNull();
			assertThat(updatedUserCoupon.getId()).isEqualTo(savedOriginalEntity.getId());
			assertThat(updatedUserCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
			assertThat(updatedUserCoupon.getUsedAt()).isEqualToIgnoringNanos(usedAt);
		}
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
			CouponEntity testCouponEntity = new CouponEntity(CouponType.FIXED, "1000원 할인 쿠폰", 1_000L, 10, expireAt);
			CouponEntity otherCoupon = jpaCouponRepository.save(testCouponEntity);
			otherCouponId = otherCoupon.getId();

			// user1에는 2개의 쿠폰 저장
			jpaUserCouponRepository.save(new UserCouponEntity(null, userId1, testCouponId, UserCouponStatus.ISSUED,
				LocalDateTime.now().minusDays(1), null));
			jpaUserCouponRepository.save(new UserCouponEntity(null, userId1, otherCouponId, UserCouponStatus.ISSUED,
				LocalDateTime.now().minusDays(2), null));
			// user2에는 1개의 쿠폰 저장
			jpaUserCouponRepository.save(new UserCouponEntity(null, userId2, testCouponId, UserCouponStatus.ISSUED,
				LocalDateTime.now().minusDays(3), null));
		}

		@Test
		@DisplayName("특정 userId에 해당하는 모든 UserCoupon 도메인 모델을 반환해야 한다")
		void findAllByUserId() {
			// when
			List<UserCoupon> foundUserCoupons = userCouponCoreRepository.findAllByUserId(userId1);

			// then
			assertThat(foundUserCoupons).hasSize(2);
			assertThat(foundUserCoupons).extracting(UserCoupon::getUserId).containsOnly(userId1);
			assertThat(foundUserCoupons).extracting(UserCoupon::getCouponId).containsExactlyInAnyOrder(testCouponId, otherCouponId);
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
			jpaUserCouponRepository.save(
				new UserCouponEntity(userId, testCouponId, UserCouponStatus.ISSUED, LocalDateTime.now(), null));
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

	@Nested
	@DisplayName("findById 메서드 테스트")
	class FindByIdTests {

		private UserCouponEntity savedUserCouponEntity;
		private Long testUserId = 40L;

		@BeforeEach
		void setUp() {
			// 테스트용 UserCouponEntity 저장
			UserCouponEntity testEntity = new UserCouponEntity(testUserId, testCouponId, UserCouponStatus.ISSUED, LocalDateTime.now(), null);
			savedUserCouponEntity = jpaUserCouponRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 ID로 조회 시 UserCoupon 도메인 모델을 반환해야 한다")
		void findById() {
			// when
			UserCoupon foundUserCoupon = userCouponCoreRepository.findById(savedUserCouponEntity.getId());

			// then
			assertThat(foundUserCoupon.getId()).isEqualTo(savedUserCouponEntity.getId());
			assertThat(foundUserCoupon.getUserId()).isEqualTo(testUserId);
			assertThat(foundUserCoupon.getCouponId()).isEqualTo(testCouponId);
			assertThat(foundUserCoupon.getIssuedAt()).isEqualToIgnoringNanos(savedUserCouponEntity.getIssuedAt());
		}

		@Test
		@DisplayName("존재하지 않는 ID로 조회 시 NOT_FOUND_USER_COUPON 에러를 반환해야 한다")
		void findById_fail_not_found() {
			// given
			Long nonExistentId = 999L;

			// when then
			assertThatThrownBy(() -> userCouponCoreRepository.findById(nonExistentId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.NOT_FOUND_USER_COUPON.getMessage());
		}
	}
}