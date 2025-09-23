package kr.hhplus.be.domain.common.event;

import java.time.LocalDateTime;

public record CouponIssuedSuccess(
	String taskId,
	Long userId,
	Long couponId,
	LocalDateTime issuedAt
) {
}