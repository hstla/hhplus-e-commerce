package kr.hhplus.be.server.config.jpa.coupon.dto;

import java.util.List;

public record UserCouponResponse(
	Long userId,
	List<CouponResponse> couponResponseList
) {
}
