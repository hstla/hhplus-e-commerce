package kr.hhplus.be.server.config.jpa.coupon.interfaces.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.coupon.application.CouponCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponRequest{

	@Getter
	@NoArgsConstructor
	public static class Publish{
		@NotNull @PositiveOrZero Long userId;
		@NotNull @PositiveOrZero Long couponId;

		private Publish(Long userId, Long couponId) {
			this.userId = userId;
			this.couponId = couponId;
		}

		public CouponCommand.Publish toCommand() {
			return CouponCommand.Publish.of(userId, couponId);
		}

		public static Publish of(Long userId, Long couponId) {
			return new Publish(userId, couponId);
		}
	}
}