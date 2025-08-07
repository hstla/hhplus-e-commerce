package kr.hhplus.be.server.config.jpa.api.usercoupon.usecase;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.JpaCouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.infrastructure.JpaUserCouponRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("FindUserCouponUseCase 통합 테스트")
class FindUserCouponUseCaseTest {

	@Autowired
	private FindUserCouponUseCase findUserCouponUseCase;
	@Autowired
	private JpaUserRepository jpaUserRepository;
	@Autowired
	private JpaCouponRepository jpaCouponRepository;
	@Autowired
	private JpaUserCouponRepository jpaUserCouponRepository;

	long userId;

	@BeforeEach
	void setUp() {
		jpaUserRepository.deleteAll();
		jpaCouponRepository.deleteAll();
		jpaUserCouponRepository.deleteAll();

		// 유저 한명에게 2개의 쿠폰 발급
		userId = jpaUserRepository.save(User.create("userName", "teste@email.com", "password")).getId();
		LocalDateTime expireAt = LocalDateTime.now().plusDays(1);
		Long couponId1 = jpaCouponRepository.save(
			Coupon.create("1000원 할인 쿠폰", CouponType.FIXED, 1_000L, 10, expireAt, 1L))
			.getId();
		Long couponId2 = jpaCouponRepository.save(
			Coupon.create("20% 할인 쿠폰", CouponType.PERCENT, 20L, 10, expireAt, 2L))
			.getId();

		LocalDateTime now = LocalDateTime.now();
		jpaUserCouponRepository.save(UserCoupon.publish(userId, couponId1, now));
		jpaUserCouponRepository.save(UserCoupon.publish(userId, couponId2, now));
	}

	@Test
	@DisplayName("사용자의 쿠폰 목록을 정상적으로 조회한다")
	void find_user_coupons_success() {
		// given when
		List<CouponResult.UserCouponInfo> result = findUserCouponUseCase.execute(userId);

		// then
		assertThat(result).hasSize(2);
		assertThat(result)
			.extracting(CouponResult.UserCouponInfo::couponType, CouponResult.UserCouponInfo::couponName)
			.containsExactlyInAnyOrder(
				tuple(CouponType.FIXED, "1000원 할인 쿠폰"),
				tuple(CouponType.PERCENT, "20% 할인 쿠폰")
			);
	}

	@Test
	@DisplayName("사용자의 보유쿠폰이 없어 빈 리스트를 반환한다")
	void findUserCoupons_empty() {
		// given
		Long otherUserId = jpaUserRepository.save(User.create("userName2", "test1@email.com", "password")).getId();

		// when
		List<CouponResult.UserCouponInfo> result = findUserCouponUseCase.execute(otherUserId);

		// then
		assertThat(result).isEmpty();
	}
}