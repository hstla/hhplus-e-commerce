package kr.hhplus.be.domain.shared.kafka;

import java.time.LocalDateTime;

public record CouponIssuedEvent(
	Long userId,
	Long couponId,
	LocalDateTime issuedAt
) {}