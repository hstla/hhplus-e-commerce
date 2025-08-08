package kr.hhplus.be.server.config.jpa.api.usercoupon.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCouponStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponResponse {

	public record UserCoupons(
		Long userId,
		List<UserCoupon> couponResponseList
	) {
		public static UserCoupons of(Long userId, List<CouponResult.UserCouponInfo> result) {
			List<UserCoupon> mapped = result.stream()
				.map(UserCoupon::of)
				.toList();
			return new UserCoupons(userId, mapped);
		}
	}

	public record UserCoupon(
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
		public static UserCoupon of(CouponResult.UserCouponInfo result) {
			return new UserCoupon(
				result.userCouponId(),
				result.couponStatus(),
				result.usedAt(),
				result.couponId(),
				result.couponName(),
				result.couponType(),
				result.discountValue(),
				result.expireAt()
			);
		}
	}
}