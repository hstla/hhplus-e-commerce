package kr.hhplus.be.server.config.jpa.api.coupon.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCouponStatus;
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

		public static UserCoupons of(Long userId, List<CouponResult.UserCouponInfo> result) {
			return new UserCoupons(userId, result.stream().map(UserCoupon::of).toList());
		}
	}

	@Getter
	@NoArgsConstructor
	public static class UserCoupon {
		// coupon info
		Long userCouponId;
		UserCouponStatus couponStatus;
		LocalDateTime usedAt;
		// userCoupon info
		Long couponId;
		String couponName;
		CouponType couponType;
		Long discountValue;
		LocalDateTime expireAt;

		public UserCoupon(Long userCouponId, UserCouponStatus couponStatus, LocalDateTime usedAt, Long couponId,
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

		public static UserCouponResponse.UserCoupon of(CouponResult.UserCouponInfo result) {
			return new UserCouponResponse.UserCoupon(result.getUserCouponId(), result.getCouponStatus(), result.getUsedAt(),
				result.getCouponId(), result.getCouponName(), result.getCouponType(), result.getDiscountValue(), result.getExpireAt());
		}
	}
}