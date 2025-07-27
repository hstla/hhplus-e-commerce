package kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponInfo {

	@Getter
	@NoArgsConstructor
	public static class Details {
		Long userId;
		Long couponId;
		private LocalDateTime publishAt;
		private LocalDateTime usedAt;

		private Details(Long userId, Long couponId, LocalDateTime publishAt, LocalDateTime usedAt) {
			this.userId = userId;
			this.couponId = couponId;
			this.publishAt = publishAt;
			this.usedAt = usedAt;
		}

		public static Details of(UserCoupon userCoupon) {
			return new Details(userCoupon.getUserId(), userCoupon.getCouponId(), userCoupon.getPublishAt(), userCoupon.getUsedAt());
		}
	}
}