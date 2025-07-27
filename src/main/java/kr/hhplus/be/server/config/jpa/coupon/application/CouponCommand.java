package kr.hhplus.be.server.config.jpa.coupon.application;

import kr.hhplus.be.server.config.jpa.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponCommand {

	@Getter
	@NoArgsConstructor
	public static class Publish{
		Long userId;
		Long couponId;

		private Publish(Long userId, Long couponId) {
			this.userId = userId;
			this.couponId = couponId;
		}

		public static Publish of(Long userId, Long couponId) {
			return new Publish(userId, couponId);
		}
	}
}
