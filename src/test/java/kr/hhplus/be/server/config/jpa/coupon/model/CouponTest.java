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

@ExtendWith(MockitoExtension.class)
@DisplayName("coupon 도메인 단위 테스트")
class CouponTest {

	@Nested
	@DisplayName("create 메서드")
	class CreateTest {

		@Test
		@DisplayName("정상 생성된다.")
		void create_success() {
			// given when
			Coupon coupon = Coupon.create(CouponType.FIXED, "5000원 할인", 5000L, 10, LocalDateTime.now().plusDays(1));

			// then
			assertThat(coupon.getId()).isNull();
			assertThat(coupon.getName()).isEqualTo("5000원 할인");
			assertThat(coupon.getDiscountValue()).isEqualTo(5000L);
			assertThat(coupon.getQuantity()).isEqualTo(10);
		}

		@Test
		@DisplayName("할인 타입이 null이면 예외 발생")
		void create_fail_invalid_type() {
			assertThatThrownBy(() ->
				Coupon.create(null, "쿠폰", 1000L, 10, LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_COUPON_TYPE.getMessage());
		}

		@Test
		@DisplayName("퍼센트 할인값이 0 이하거나 100 초과면 예외 발생")
		void create_fail_invalid_percent_discount() {
			assertThatThrownBy(() ->
				Coupon.create(CouponType.PERCENT, "퍼센트 쿠폰", 0L, 10, LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_PERCENT_DISCOUNT.getMessage());

			assertThatThrownBy(() ->
				Coupon.create(CouponType.PERCENT, "퍼센트 쿠폰", 101L, 10, LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_PERCENT_DISCOUNT.getMessage());
		}

		@Test
		@DisplayName("정액 할인값이 0 이하이면 예외 발생")
		void create_fail_invalid_fixed_discount() {
			assertThatThrownBy(() ->
				Coupon.create(CouponType.FIXED, "정액 쿠폰", 0L, 10, LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INVALID_FIXED_DISCOUNT.getMessage());
		}
	}

	@Nested
	@DisplayName("validateForPublish 메서드")
	class PublishValidationTest {

		@Test
		@DisplayName("재고가 없으면 예외 발생")
		void publish_fail_out_of_stock() {
			// given
			Coupon coupon = Coupon.create(CouponType.FIXED, "쿠폰", 1000L, 0, LocalDateTime.now().plusDays(1));

			// when then
			assertThatThrownBy(() -> coupon.validateForPublish(LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.OUT_OF_STOCK_COUPON.getMessage());
		}

		@Test
		@DisplayName("쿠폰이 만료되었으면 예외 발생")
		void publish_fail_expired() {
			// given
			Coupon coupon = Coupon.create(CouponType.FIXED, "쿠폰", 1000L, 10, LocalDateTime.now().minusDays(1));

			// when then
			assertThatThrownBy(() -> coupon.validateForPublish(LocalDateTime.now()))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.EXPIRED_COUPON.getMessage());
		}
	}

	@Test
	@DisplayName("decreaseQuantity는 수량을 1 감소시킨다")
	void decreaseQuantity_success() {
		// given
		Coupon coupon = Coupon.create(CouponType.FIXED, "쿠폰", 1000L, 3, LocalDateTime.now().plusDays(1));

		// when
		coupon.decreaseQuantity();

		// then
		assertThat(coupon.getQuantity()).isEqualTo(2);
	}
}