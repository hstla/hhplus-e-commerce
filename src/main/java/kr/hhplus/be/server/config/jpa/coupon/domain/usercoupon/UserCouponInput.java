package kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon;

import kr.hhplus.be.server.config.jpa.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponInput {

	@Getter
	@NoArgsConstructor
	public static class Publish {
		User user;
		Long couponId;

		private Publish(User user, Long couponId) {
			this.user = user;
			this.couponId = couponId;
		}

		public static Publish of(User user, Long couponId) {
			return new Publish(user, couponId);
		}
	}
}