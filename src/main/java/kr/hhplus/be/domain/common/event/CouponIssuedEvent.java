package kr.hhplus.be.domain.common.event;

import java.time.LocalDateTime;

public record CouponIssuedEvent(
	String taskId,
	Long userId,
	Long couponId,
	LocalDateTime issuedAt
) {}