package kr.hhplus.be.server.config.jpa.coupon.model;

import java.time.LocalDateTime;

import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coupon {
	private Long id;
	private CouponType discountType;
	private String name;
	private Long discountValue;
	private int quantity;
	private LocalDateTime expireAt;

	public static Coupon create(CouponType discountType, String name,
		Long discountValue, int quantity, LocalDateTime expireAt) {
		validateDiscountValue(discountType, discountValue);
		return new Coupon(null, discountType, name, discountValue, quantity, expireAt);
	}

	public void validateForPublish(LocalDateTime nowDateTime) {
		validateStock();
		validateNotExpired(nowDateTime);
	}

	public void decreaseQuantity() {
		this.quantity--;
	}

	public void validateNotExpired(LocalDateTime nowDateTime) {
		if (this.expireAt == null || this.expireAt.isBefore(nowDateTime)) {
			throw new RestApiException(CouponErrorCode.EXPIRED_COUPON);
		}
	}

	private void validateStock() {
		if (this.quantity < 1) {
			throw new RestApiException(CouponErrorCode.OUT_OF_STOCK_COUPON);
		}
	}

	private static void validateDiscountValue(CouponType discountType, Long discountValue) {
		if (discountType == null) {
			throw new RestApiException(CouponErrorCode.INVALID_COUPON_TYPE);
		}
		if (discountType == CouponType.PERCENT) {
			validatePercentDiscountValue(discountValue);
		} else if (discountType == CouponType.FIXED) {
			if (discountValue <= 0) {
				throw new RestApiException(CouponErrorCode.INVALID_FIXED_DISCOUNT);
			}
		}
	}

	private static void validatePercentDiscountValue(Long discountValue) {
		if (discountValue <= 0 || discountValue > 100) {
			throw new RestApiException(CouponErrorCode.INVALID_PERCENT_DISCOUNT);
		}
	}
}