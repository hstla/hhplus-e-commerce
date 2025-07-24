package kr.hhplus.be.server.config.jpa.coupon.application;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponInfo;
import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResult {

	@Getter
	@NoArgsConstructor
	public static class Coupon {
		Long couponId;
		String couponName;
		CouponType couponType;
		int discountValue;
		LocalDateTime expireAt;

		private Coupon(Long couponId, String couponName, CouponType couponType,
			int discountValue, LocalDateTime expireAt
		) {
			this.couponId = couponId;
			this.couponName = couponName;
			this.couponType = couponType;
			this.discountValue = discountValue;
			this.expireAt = expireAt;
		}

		public static Coupon of(CouponInfo.Info info) {
			return new Coupon(info.getId(), info.getName(), info.getDiscountType(), info.getDiscountValue(), info.getExpireAt());
		}
	}

	@Getter
	@NoArgsConstructor
	public static class UserCoupon {
		Long couponId;
		String couponName;
		CouponType couponType;
		int discountValue;
		LocalDateTime expireAt;
		LocalDateTime usedAt;

		private UserCoupon(Long couponId, String couponName, CouponType couponType, int discountValue,
			LocalDateTime expireAt,
			LocalDateTime usedAt) {
			this.couponId = couponId;
			this.couponName = couponName;
			this.couponType = couponType;
			this.discountValue = discountValue;
			this.expireAt = expireAt;
			this.usedAt = usedAt;
		}

		public static UserCoupon of(CouponInfo.Info info, LocalDateTime usedAt) {
			return new UserCoupon(info.getId(), info.getName(), info.getDiscountType(), info.getDiscountValue(), info.getExpireAt(), usedAt);
		}
	}
}