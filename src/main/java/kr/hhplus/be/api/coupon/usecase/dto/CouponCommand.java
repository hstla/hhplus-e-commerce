package kr.hhplus.be.api.coupon.usecase.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.domain.coupon.model.CouponType;

public class CouponCommand {

	public record CouponCreate(
		String name,
		CouponType couponType,
		Long discountValue,
		int initialStock,
		LocalDateTime expireAt
	) {
		public static CouponCommand.CouponCreate of(String name, CouponType couponType,
			Long discountValue, int initialStock, LocalDateTime expireAt) {
			return new CouponCommand.CouponCreate(name, couponType, discountValue, initialStock, expireAt);
		}
	}
}