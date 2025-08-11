package kr.hhplus.be.server.config.jpa.coupon.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.DiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.FixedDiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.PercentDiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCouponStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponDiscountService 단위 테스트")
class CouponDiscountServiceTest {

	@InjectMocks
	private CouponDiscountService couponService;

	private Map<CouponType, DiscountPolicy> discountPolicies;

	private final DiscountPolicy fixedDiscountPolicy = new FixedDiscountPolicy();
	private final DiscountPolicy percentDiscountPolicy = new PercentDiscountPolicy();

	@BeforeEach
	void setUp() {
		discountPolicies = new HashMap<>();
		discountPolicies.put(CouponType.FIXED, fixedDiscountPolicy);
		discountPolicies.put(CouponType.PERCENT, percentDiscountPolicy);

		couponService = new CouponDiscountService(discountPolicies);
	}

	@Nested
	@DisplayName("calculateDiscount 메서드는")
	class calculateDiscountTest {

		@Test
		@DisplayName("고정 금액 할인 쿠폰을 성공적으로 적용한다")
		void useFixedCouponSuccess() {
			// given
			long originPrice = 10_000L;
			LocalDateTime now = LocalDateTime.now();

			UserCoupon userCoupon = UserCoupon.publish(100L, 200L, now.minusDays(1));
			Coupon coupon = new Coupon(200L, "Fixed Coupon", CouponType.FIXED, 2_000L, 10, now.plusDays(1));

			// when
			long discountedAmount = couponService.calculateDiscount(userCoupon, coupon, originPrice, now);

			// then
			assertThat(discountedAmount).isEqualTo(coupon.getDiscountValue());
			assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
			assertThat(userCoupon.getUsedAt()).isEqualTo(now);
		}

		@Test
		@DisplayName("퍼센트 할인 쿠폰을 성공적으로 적용한다")
		void usePercentCouponSuccess() {
			// given
			long totalAmount = 10_000L;
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime usedAt = now.plusDays(1);

			UserCoupon userCoupon = UserCoupon.publish(100L, 201L, now.minusDays(1));
			Coupon coupon = new Coupon(201L, "Percent Coupon", CouponType.PERCENT, 20L, 10, usedAt);

			// when
			long discountedAmount = couponService.calculateDiscount(userCoupon, coupon, totalAmount, now);

			// then
			assertThat(discountedAmount).isEqualTo(totalAmount * coupon.getDiscountValue() / 100);
			assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
			assertThat(userCoupon.getUsedAt()).isEqualTo(now);
		}
	}
}