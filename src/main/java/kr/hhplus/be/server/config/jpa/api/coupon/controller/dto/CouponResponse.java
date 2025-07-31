package kr.hhplus.be.server.config.jpa.api.coupon.controller.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

	@Getter
	@NoArgsConstructor
	public static class Coupon {
		Long couponId;
		String couponName;
		CouponType couponType;
		Long discountValue;
		LocalDateTime expireAt;

		private Coupon(Long couponId, String couponName, CouponType couponType,
			Long discountValue, LocalDateTime expireAt) {
			this.couponId = couponId;
			this.couponName = couponName;
			this.couponType = couponType;
			this.discountValue = discountValue;
			this.expireAt = expireAt;
		}

		public static Coupon of(CouponResult.CouponInfo result) {
			return new Coupon(result.getCouponId(), result.getCouponName(), result.getCouponType(), result.getDiscountValue(), result.getExpireAt());
		}
	}
}