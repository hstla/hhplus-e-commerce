package kr.hhplus.be.server.config.jpa.usercoupon.usecase;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.PublishCouponUseCase;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.UserCouponCommand;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.user.component.UserValidator;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublishCouponUseCase 단위 테스트")
class PublishCouponUseCaseTest {

	@Mock
	private UserValidator userValidator;

	@Mock
	private CouponService couponService;

	@InjectMocks
	private PublishCouponUseCase publishCouponUseCase;

	@Nested
	@DisplayName("publish 메서드")
	class Publish {

		@Test
		@DisplayName("정상적인 유저 ID가 주어지면 쿠폰을 발행한다")
		void execute_success() {
			// given
			long userId = 1L;
			long couponId = 1L;
			LocalDateTime now = LocalDateTime.now();
			UserCouponCommand.Publish publish = UserCouponCommand.Publish.of(userId, couponId);
			Coupon coupon = new Coupon(couponId, CouponType.FIXED, "10% 할인 쿠폰", 10L, 10, now);
			given(couponService.publishCoupon(eq(userId), eq(couponId), any(LocalDateTime.class))).willReturn(coupon);

			// when
			CouponResult.CouponInfo couponInfo = publishCouponUseCase.execute(publish);

			// then
			assertThat(couponInfo.getCouponId()).isEqualTo(couponId);
			assertThat(couponInfo.getCouponName()).isEqualTo("10% 할인 쿠폰");
			assertThat(couponInfo.getCouponType()).isEqualTo(CouponType.FIXED);
			assertThat(couponInfo.getDiscountValue()).isEqualTo(10);
			assertThat(couponInfo.getExpireAt()).isEqualTo(now);

			verify(userValidator, times(1)).validateExistingUser(userId);
		}
	}
}