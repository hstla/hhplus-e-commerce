package kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCouponStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResult {

	public record CouponInfo(
		Long couponId,
		String couponName,
		CouponType couponType,
		Long discountValue,
		LocalDateTime expireAt
	) {
		public static CouponInfo of(Coupon coupon) {
			return new CouponInfo(
				coupon.getId(),
				coupon.getName(),
				coupon.getDiscountType(),
				coupon.getDiscountValue(),
				coupon.getExpireAt()
			);
		}
	}

	public record UserCouponInfo(
		// userCoupon fields
		Long userCouponId,
		UserCouponStatus couponStatus,
		LocalDateTime usedAt,
		// coupon fields
		Long couponId,
		String couponName,
		CouponType couponType,
		Long discountValue,
		LocalDateTime expireAt
	) {
		public static UserCouponInfo of(UserCoupon userCoupon, Coupon coupon) {
			return new UserCouponInfo(
				userCoupon.getId(),
				userCoupon.getStatus(),
				userCoupon.getUsedAt(),
				coupon.getId(),
				coupon.getName(),
				coupon.getDiscountType(),
				coupon.getDiscountValue(),
				coupon.getExpireAt()
			);
		}
	}
}