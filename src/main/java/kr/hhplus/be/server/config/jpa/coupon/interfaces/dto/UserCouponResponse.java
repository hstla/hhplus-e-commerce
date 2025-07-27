package kr.hhplus.be.server.config.jpa.coupon.interfaces.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.config.jpa.coupon.application.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponResponse {

	@Getter
	@NoArgsConstructor
	public static class UserCoupons {
		Long userId;
		List<UserCoupon> couponResponseList;

		private UserCoupons(Long userId, List<UserCoupon> couponResponseList) {
			this.userId = userId;
			this.couponResponseList = couponResponseList;
		}

		public static UserCoupons of(Long userId, List<CouponResult.UserCoupon> result) {
			return new UserCoupons(userId, result.stream().map(UserCoupon::of).toList());
		}
	}

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

		public static UserCouponResponse.UserCoupon of(CouponResult.UserCoupon result) {
			return new UserCouponResponse.UserCoupon(result.getCouponId(), result.getCouponName(),
				result.getCouponType(), result.getDiscountValue(), result.getExpireAt(), result.getUsedAt());
		}
	}
}