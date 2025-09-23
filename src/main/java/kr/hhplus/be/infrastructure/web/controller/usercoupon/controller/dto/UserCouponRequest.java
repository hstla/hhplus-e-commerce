package kr.hhplus.be.infrastructure.web.controller.usercoupon.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.application.usercoupon.dto.UserCouponCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponRequest {

	public record Publish(
		@NotNull @PositiveOrZero Long userId,
		@NotNull @PositiveOrZero Long couponId
	) {
		public UserCouponCommand.Publish toCommand() {
			return UserCouponCommand.Publish.of(userId, couponId);
		}

		public static Publish of(Long userId, Long couponId) {
			return new Publish(userId, couponId);
		}
	}
}