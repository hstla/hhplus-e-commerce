package kr.hhplus.be.server.config.jpa.api.usercoupon.controller.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

	public record Coupon(
		Long couponId,
		String couponName,
		CouponType couponType,
		Long discountValue,
		LocalDateTime expireAt
	) {
		public static Coupon of(CouponResult.CouponInfo result) {
			return new Coupon(
				result.couponId(),
				result.couponName(),
				result.couponType(),
				result.discountValue(),
				result.expireAt()
			);
		}
	}
}