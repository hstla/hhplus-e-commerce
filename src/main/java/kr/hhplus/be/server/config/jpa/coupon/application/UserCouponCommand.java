package kr.hhplus.be.server.config.jpa.coupon.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponCommand {

	@Getter
	@NoArgsConstructor
	public static class Publish{
		private Long userId;
		private Long couponId;

		private Publish(Long userId, Long couponId) {
			this.userId = userId;
			this.couponId = couponId;
		}

		public static Publish of(Long userId, Long couponId) {
			return new Publish(userId, couponId);
		}
	}

}
