package kr.hhplus.be.server.config.jpa.coupon.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.coupon.CouponType;

public record CouponResponse(
	Long couponId,
	String couponName,
	CouponType couponType,
	int discountValue,
	boolean isUsed,
	LocalDateTime expireAt
) {
}
