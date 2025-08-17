package kr.hhplus.be.domain.usercoupon.component;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCouponValidator 단위 테스트")
class UserCouponValidatorTest {

	@Mock
	private UserCouponRepository userCouponRepository;
	@InjectMocks
	private UserCouponValidator userCouponValidator;

	@Nested
	@DisplayName("validateUserDoesNotHaveCoupon 테스트 시")
	class validateUserDoesNotHaveCouponTest {

		@Test
		@DisplayName("유저가 쿠폰을 보유하고 있지 않다면 예외를 던지지 않는다")
		void validateUserDoesNotHaveCoupon() {
			// given
			Long userId = 2L;
			Long couponId = 20L;
			given(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).willReturn(false);

			// when then
			assertDoesNotThrow(() -> userCouponValidator.validateUserDoesNotHaveCoupon(userId, couponId));
		}

		@Test
		@DisplayName("유저가 이미 쿠폰을 보유하고 있다면 예외를 던진다")
		void validateUserDoesNotHaveCoupon_fail() {
			// given
			Long userId = 1L;
			Long couponId = 10L;
			given(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).willReturn(true);

			// when then
			assertThatThrownBy(() -> userCouponValidator.validateUserDoesNotHaveCoupon(userId, couponId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.DUPLICATED_COUPON.getMessage());
		}
	}
}