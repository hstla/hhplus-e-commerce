package kr.hhplus.be.server.config.jpa.coupon.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCouponStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCoupon 도메인 테스트")
class UserCouponTest {

	@Nested
	@DisplayName("publish 메서드는")
	class Publish {

		@Test
		@DisplayName("정상적으로 발급된 쿠폰을 생성한다")
		void publishSuccess() {
			// given
			Long userId = 1L;
			Long couponId = 100L;
			LocalDateTime now = LocalDateTime.now();

			// when
			UserCoupon userCoupon = UserCoupon.publish(userId, couponId, now);

			// then
			assertThat(userCoupon.getUserId()).isEqualTo(userId);
			assertThat(userCoupon.getCouponId()).isEqualTo(couponId);
			assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
			assertThat(userCoupon.getIssuedAt()).isEqualTo(now);
			assertThat(userCoupon.getUsedAt()).isNull();
		}
	}

	@Nested
	@DisplayName("use 메서드는")
	class Use {

		@Test
		@DisplayName("정상적으로 쿠폰을 사용할 수 있다")
		void useSuccess() {
			// given
			UserCoupon userCoupon = UserCoupon.publish(1L, 100L, LocalDateTime.now());
			LocalDateTime usedAt = LocalDateTime.now();

			// when
			userCoupon.use(usedAt);

			// then
			assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
			assertThat(userCoupon.getUsedAt()).isEqualTo(usedAt);
		}

		@Test
		@DisplayName("이미 사용한 쿠폰이면 예외를 던진다")
		void alreadyUsedCoupon() {
			// given
			UserCoupon userCoupon = UserCoupon.publish(1L, 100L, LocalDateTime.now());
			userCoupon.use(LocalDateTime.now());

			// when & then
			assertThatThrownBy(() -> userCoupon.use(LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_COUPON_TYPE.getMessage());
		}

		@Test
		@DisplayName("쿠폰 상태가 ISSUED가 아니면 예외를 던진다")
		void notIssuedStatus() {
			// given
			UserCoupon userCoupon = new UserCoupon(
				1L,
				1L,
				100L,
				UserCouponStatus.USED,
				LocalDateTime.now(),
				null
			);

			// when & then
			assertThatThrownBy(() -> userCoupon.use(LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_COUPON_TYPE.getMessage());
		}
	}
}