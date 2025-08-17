package kr.hhplus.be.api.coupon.usecase.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;

public class CouponResult {

	public record CouponDetail(
		Long couponId,
		String name,
		CouponType couponType,
		Long discountValue,
		int initialStock,
		LocalDateTime expireAt
	) {
		public static CouponDetail of(Coupon coupon) {
			return new CouponDetail(coupon.getId(), coupon.getName(), coupon.getDiscountType(),
				coupon.getDiscountValue(), coupon.getInitialStock(), coupon.getExpireAt());
		}
	}
}