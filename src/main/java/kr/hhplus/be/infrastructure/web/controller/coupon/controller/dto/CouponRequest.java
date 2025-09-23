package kr.hhplus.be.infrastructure.web.controller.coupon.controller.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.application.coupon.dto.CouponCommand;
import kr.hhplus.be.domain.coupon.model.CouponType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponRequest {

	public record Coupon (
		String name,
		CouponType couponType,
		Long discountValue,
		int initialStock,
		LocalDateTime expireAt
	) {
		public static Coupon of(String name, CouponType couponType, Long discountValue, int initialStock, LocalDateTime expireAt) {
			return new Coupon(name, couponType, discountValue, initialStock, expireAt);
		}

		public CouponCommand.CouponCreate toCommand() {
			return CouponCommand.CouponCreate.of(name, couponType, discountValue, initialStock, expireAt);
		}
	}
}