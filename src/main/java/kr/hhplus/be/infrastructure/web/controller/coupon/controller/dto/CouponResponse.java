package kr.hhplus.be.infrastructure.web.controller.coupon.controller.dto;

import java.time.LocalDateTime;

import kr.hhplus.be.application.coupon.dto.CouponResult;
import kr.hhplus.be.domain.coupon.model.CouponType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

	public record Coupon(
		Long couponId,
		String couponName,
		CouponType couponType,
		Long discountValue,
		LocalDateTime expireAt
	) {
		public static Coupon of(CouponResult.CouponDetail result) {
			return new Coupon(
				result.couponId(),
				result.name(),
				result.couponType(),
				result.discountValue(),
				result.expireAt()
			);
		}
	}
}