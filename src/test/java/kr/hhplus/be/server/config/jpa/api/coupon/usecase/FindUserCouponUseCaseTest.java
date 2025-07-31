package kr.hhplus.be.server.config.jpa.api.coupon.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCouponStatus;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserCouponUseCase 단위 테스트")
class FindUserCouponUseCaseTest {

	@InjectMocks
	private FindUserCouponUseCase findUserCouponUseCase;

	@Mock
	private UserCouponRepository userCouponRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private UserRepository userRepository;

	private final Long userId = 1L;
	private final Long couponId = 100L;
	private UserCoupon userCoupon;
	private Coupon coupon;

	@BeforeEach
	void setUp() {
		userCoupon = new UserCoupon(
			1L,
			userId,
			couponId,
			UserCouponStatus.ISSUED,
			LocalDateTime.of(2025, 1, 1, 0, 0),
			null
		);

		coupon = new Coupon(
			couponId,
			CouponType.PERCENT,
			"10% 할인쿠폰",
			10L,
			10,
			LocalDateTime.of(2026, 1, 1, 0, 0)
		);
	}

	@Test
	@DisplayName("사용자의 쿠폰 목록을 정상적으로 조회한다")
	void findUserCoupons_Success() {
		// given
		given(userRepository.findById(userId)).willReturn(mock(User.class));
		given(userCouponRepository.findAllByUserId(userId)).willReturn(List.of(userCoupon));
		given(couponRepository.findById(couponId)).willReturn(coupon);

		// when
		List<CouponResult.UserCouponInfo> result = findUserCouponUseCase.execute(userId);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getCouponId()).isEqualTo(couponId);
		assertThat(result.get(0).getCouponName()).isEqualTo("10% 할인쿠폰");
	}

	@Test
	@DisplayName("사용자의 보유쿠폰이 없어 빈 리스트를 반환한다")
	void findUserCoupons_empty() {
		// given
		given(userRepository.findById(userId)).willReturn(mock(User.class));
		given(userCouponRepository.findAllByUserId(userId)).willReturn(List.of());

		// when
		List<CouponResult.UserCouponInfo> result = findUserCouponUseCase.execute(userId);

		// then
		assertThat(result).isEmpty();
	}
}