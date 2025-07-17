package kr.hhplus.be.server.config.jpa.coupon.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CouponPublishRequest(
	@NotNull @PositiveOrZero Long userId,
	@NotNull @PositiveOrZero Long couponId
) {
}
