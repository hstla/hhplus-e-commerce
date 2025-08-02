package kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCouponStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResult {

	@Getter
	@NoArgsConstructor
	public static class CouponInfo {
		Long couponId;
		String couponName;
		CouponType couponType;
		Long discountValue;
		LocalDateTime expireAt;

		private CouponInfo(Long couponId, String couponName, CouponType couponType,
			Long discountValue, LocalDateTime expireAt
		) {
			this.couponId = couponId;
			this.couponName = couponName;
			this.couponType = couponType;
			this.discountValue = discountValue;
			this.expireAt = expireAt;
		}

		public static CouponInfo of(Coupon coupon) {
			return new CouponInfo(coupon.getId(), coupon.getName(), coupon.getDiscountType(), coupon.getDiscountValue(), coupon.getExpireAt());
		}
	}

	@Getter
	@NoArgsConstructor
	public static class UserCouponInfo {
		// UserCoupon fields
		Long userCouponId;
		UserCouponStatus couponStatus;
		LocalDateTime usedAt;
		// coupon fields
		Long couponId;
		String couponName;
		CouponType couponType;
		Long discountValue;
		LocalDateTime expireAt;

		private UserCouponInfo(Long userCouponId, UserCouponStatus couponStatus, LocalDateTime usedAt, Long couponId,
			String couponName, CouponType couponType, Long discountValue, LocalDateTime expireAt) {
			this.userCouponId = userCouponId;
			this.couponStatus = couponStatus;
			this.usedAt = usedAt;
			this.couponId = couponId;
			this.couponName = couponName;
			this.couponType = couponType;
			this.discountValue = discountValue;
			this.expireAt = expireAt;
		}

		public static UserCouponInfo of(UserCoupon userCoupon, Coupon coupon) {
			return new UserCouponInfo(userCoupon.getId(), userCoupon.getStatus(), userCoupon.getUsedAt(),
				coupon.getId(), coupon.getName(), coupon.getDiscountType(), coupon.getDiscountValue(), coupon.getExpireAt());
		}
	}
}