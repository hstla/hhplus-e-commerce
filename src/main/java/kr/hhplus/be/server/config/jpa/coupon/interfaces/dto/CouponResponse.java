package kr.hhplus.be.server.config.jpa.coupon.interfaces.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.coupon.application.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

	@NoArgsConstructor
	public static class Coupon {
		Long couponId;
		String couponName;
		CouponType couponType;
		int discountValue;
		LocalDateTime expireAt;

		private Coupon(Long couponId, String couponName, CouponType couponType,
			int discountValue, LocalDateTime expireAt) {
			this.couponId = couponId;
			this.couponName = couponName;
			this.couponType = couponType;
			this.discountValue = discountValue;
			this.expireAt = expireAt;
		}

		public static Coupon of(CouponResult.Coupon result) {
			return new Coupon(result.getCouponId(), result.getCouponName(), result.getCouponType(), result.getDiscountValue(), result.getExpireAt());
		}
	}
}