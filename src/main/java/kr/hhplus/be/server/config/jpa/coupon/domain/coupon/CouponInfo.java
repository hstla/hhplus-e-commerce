package kr.hhplus.be.server.config.jpa.coupon.domain.coupon;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponInfo {

	@Getter
	@NoArgsConstructor
	public static class Info {
		private Long id;
		private CouponType discountType;
		private CouponStatus status;
		private String name;
		private int discountValue;
		private int quantity;
		private LocalDateTime expireAt;

		private Info(Long id, CouponType discountType, CouponStatus status, String name, int discountValue, int quantity,
			LocalDateTime expireAt) {
			this.id = id;
			this.discountType = discountType;
			this.status = status;
			this.name = name;
			this.discountValue = discountValue;
			this.quantity = quantity;
			this.expireAt = expireAt;
		}

		public static Info of(Coupon coupon) {
			return new Info(coupon.getId(), coupon.getDiscountType(), coupon.getStatus(), coupon.getName(),
				coupon.getDiscountValue(), coupon.getQuantity(), coupon.getExpireAt());
		}
	}
}