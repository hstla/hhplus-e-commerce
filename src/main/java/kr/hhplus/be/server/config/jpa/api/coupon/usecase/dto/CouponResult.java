package kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;

public class CouponResult {

	public record Info(
		Long couponId,
		String name,
		CouponType couponType,
		Long discountValue,
		int initialStock,
		LocalDateTime expireAt
	) {
		public static Info of(Coupon coupon) {
			return new Info(coupon.getId(), coupon.getName(), coupon.getDiscountType(),
				coupon.getDiscountValue(), coupon.getInitialStock(), coupon.getExpireAt());
		}
	}
}
