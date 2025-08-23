package kr.hhplus.be.domain.coupon.model;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;

@ExtendWith(MockitoExtension.class)
@DisplayName("coupon 도메인 단위 테스트")
class CouponTest {

	@Nested
	@DisplayName("create 메서드")
	class CreateTest {

		@Test
		@DisplayName("쿠폰이 정상 생성된다.")
		void create_success() {
			// given
			LocalDateTime expireAt = LocalDateTime.now().plusDays(1);

			// when
			Coupon coupon = Coupon.create("5000원 할인", CouponType.FIXED, 5_000L, 10, expireAt);

			// then
			assertSoftly(soft -> {
				soft.assertThat(coupon.getId()).isNull();
				soft.assertThat(coupon.getName()).isEqualTo("5000원 할인");
				soft.assertThat(coupon.getDiscountValue()).isEqualTo(5000L);
				soft.assertThat(coupon.getInitialStock()).isEqualTo(10);
			});
		}

		@ParameterizedTest
		@ValueSource(longs = {0L, 101L})
		@DisplayName("퍼센트 할인값이 0 이하거나 100 초과면 예외 발생")
		void create_fail_invalid_percent_discount(long discountValue) {
			assertThatThrownBy(() ->
				Coupon.create("퍼센트 쿠폰", CouponType.PERCENT, discountValue, 10, LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_PERCENT_DISCOUNT.getMessage());
		}

		@Test
		@DisplayName("정액 할인값이 0 이하이면 예외 발생")
		void create_fail_invalid_fixed_discount() {
			assertThatThrownBy(() ->
				Coupon.create("정액 쿠폰", CouponType.FIXED, 0L, 10, LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_FIXED_DISCOUNT.getMessage());
		}
	}
}