package kr.hhplus.be.api.usercoupon.usecase;

import java.time.LocalDateTime;

public record DbSyncTask(
	long userId,
	long couponId,
	LocalDateTime publishTime
) {
	public static DbSyncTask of(long userId, long couponId) {
		return new DbSyncTask(userId, couponId, LocalDateTime.now());
	}
}
