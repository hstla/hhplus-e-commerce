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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.usercoupon.component.UserCouponValidator;
import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.DiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.FixedDiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.compnent.discount.PercentDiscountPolicy;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService 단위 테스트")
class CouponServiceTest {

	@InjectMocks
	private CouponService couponService;

	@Mock
	private UserCouponRepository userCouponRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private UserCouponValidator userCouponValidator;

	private Map<CouponType, DiscountPolicy> discountPolicies;

	private final DiscountPolicy fixedDiscountPolicy = new FixedDiscountPolicy();
	private final DiscountPolicy percentDiscountPolicy = new PercentDiscountPolicy();

	@BeforeEach
	void setUp() {
		discountPolicies = new HashMap<>();
		discountPolicies.put(CouponType.FIXED, fixedDiscountPolicy);
		discountPolicies.put(CouponType.PERCENT, percentDiscountPolicy);

		couponService = new CouponService(
			userCouponRepository,
			couponRepository,
			userCouponValidator,
			discountPolicies
		);
	}

	@Nested
	@DisplayName("useUserCoupon 메서드는")
	class UseUserCoupon {

		@Test
		@DisplayName("고정 금액 할인 쿠폰을 성공적으로 적용한다")
		void useFixedCouponSuccess() {
			// given
			long userCouponId = 1L;
			long totalAmount = 10_000L;
			LocalDateTime now = LocalDateTime.now();

			UserCoupon userCoupon = UserCoupon.publish(100L, 200L, now.minusDays(1));
			Coupon coupon = new Coupon(200L, CouponType.FIXED, "Fixed Coupon", 2_000L, 10, now.plusDays(1));

			given(userCouponRepository.findById(userCouponId)).willReturn(userCoupon);
			given(couponRepository.findById(userCoupon.getCouponId())).willReturn(coupon);

			// when
			long discountedAmount = couponService.calculateCoupon(userCouponId, totalAmount, now);

			// then
			assertThat(discountedAmount).isEqualTo(coupon.getDiscountValue());
			verify(userCouponRepository).save(userCoupon);
		}

		@Test
		@DisplayName("퍼센트 할인 쿠폰을 성공적으로 적용한다")
		void usePercentCouponSuccess() {
			// given
			long userCouponId = 1L;
			long totalAmount = 10_000L;
			LocalDateTime now = LocalDateTime.now();

			UserCoupon userCoupon = UserCoupon.publish(100L, 201L, now.minusDays(1));
			Coupon coupon = new Coupon(201L, CouponType.PERCENT, "Percent Coupon", 20L, 10, now.plusDays(1));

			given(userCouponRepository.findById(userCouponId)).willReturn(userCoupon);
			given(couponRepository.findById(userCoupon.getCouponId())).willReturn(coupon);

			// when
			long discountedAmount = couponService.calculateCoupon(userCouponId, totalAmount, now);

			// then
			assertThat(discountedAmount).isEqualTo(totalAmount * coupon.getDiscountValue() / 100);
			verify(userCouponRepository).save(userCoupon);
		}

		@Test
		@DisplayName("쿠폰이 만료되어 실패한다")
		void useCouponExpiredFail() {
			// given
			long userCouponId = 1L;
			long totalAmount = 10_000L;
			LocalDateTime now = LocalDateTime.now();

			UserCoupon userCoupon = UserCoupon.publish(100L, 300L, now.minusDays(10));
			// expireAt이 now보다 과거
			Coupon expiredCoupon = new Coupon(300L, CouponType.FIXED, "Expired Coupon", 1_000L, 10, now.minusDays(1));

			given(userCouponRepository.findById(userCouponId)).willReturn(userCoupon);
			given(couponRepository.findById(userCoupon.getCouponId())).willReturn(expiredCoupon);

			// when then
			assertThatThrownBy(() -> couponService.calculateCoupon(userCouponId, totalAmount, now))
				.isInstanceOf(RestApiException.class)
				.hasMessageContaining(CouponErrorCode.EXPIRED_COUPON.getMessage());

			verify(userCouponRepository).findById(userCouponId);
			verify(couponRepository).findById(expiredCoupon.getId());
			verify(userCouponRepository, times(1)).save(any());
		}
	}

	@Nested
	@DisplayName("publishCoupon 메서드는")
	class PublishCoupon {

		@Test
		@DisplayName("쿠폰을 정상적으로 발급한다")
		void publishCouponSuccess() {
			// given
			Long userId = 1L;
			Long couponId = 100L;
			LocalDateTime now = LocalDateTime.now();

			Coupon coupon = new Coupon(couponId, CouponType.FIXED, "Welcome Coupon", 1000L, 5, now.plusDays(10));

			// 기존 수량이 5이므로 1 감소하면 4가 되어야 함
			given(couponRepository.findById(couponId)).willReturn(coupon);

			// validateUserDoesNotHaveCoupon 은 void 메서드
			willDoNothing().given(userCouponValidator).validateUserDoesNotHaveCoupon(userId, couponId);
			given(couponRepository.save(coupon)).willReturn(coupon);

			// when
			Coupon result = couponService.publishCoupon(userId, couponId, now);

			// then
			assertThat(result.getQuantity()).isEqualTo(4); // 수량 감소 확인
			verify(userCouponRepository).save(any(UserCoupon.class));
			verify(couponRepository).save(coupon);
		}

		@Test
		@DisplayName("쿠폰 수량이 0이면 발급에 실패한다")
		void publishCouponFail_dueToZeroQuantity() {
			// given
			Long userId = 1L;
			Long couponId = 200L;
			LocalDateTime now = LocalDateTime.now();

			// 수량 0
			Coupon coupon = new Coupon(couponId, CouponType.FIXED, "Zero Coupon", 1000L, 0, now.plusDays(10));

			given(couponRepository.findById(couponId)).willReturn(coupon);
			willDoNothing().given(userCouponValidator).validateUserDoesNotHaveCoupon(userId, couponId);

			// when then
			assertThatThrownBy(() -> couponService.publishCoupon(userId, couponId, now))
				.isInstanceOf(RestApiException.class)
				.hasMessageContaining(CouponErrorCode.OUT_OF_STOCK_COUPON.getMessage());

			verify(userCouponRepository, never()).save(any());
			verify(couponRepository, never()).save(any());
		}
	}
}