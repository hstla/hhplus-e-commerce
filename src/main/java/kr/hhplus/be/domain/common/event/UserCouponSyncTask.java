package kr.hhplus.be.domain.common.event;

import java.time.LocalDateTime;

public record UserCouponSyncTask(
	long userId,
	long couponId,
	LocalDateTime publishTime
) {
	public static UserCouponSyncTask of(long userId, long couponId) {
		return new UserCouponSyncTask(userId, couponId, LocalDateTime.now());
	}
}