package kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponCommand {

	public record Publish(Long userId, Long couponId) {
		public static Publish of(Long userId, Long couponId) {
			return new Publish(userId, couponId);
		}
	}
}