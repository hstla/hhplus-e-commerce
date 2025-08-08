package kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;

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