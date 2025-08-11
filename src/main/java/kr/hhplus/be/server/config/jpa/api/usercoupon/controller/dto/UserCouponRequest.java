package kr.hhplus.be.server.config.jpa.api.usercoupon.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.UserCouponCommand;
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