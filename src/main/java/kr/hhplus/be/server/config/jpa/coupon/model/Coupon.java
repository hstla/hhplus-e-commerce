package kr.hhplus.be.server.config.jpa.coupon.domain.coupon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id", unique = true)
	private Long id;
	@Enumerated(EnumType.STRING)
	private CouponType discountType;
	@Enumerated(EnumType.STRING)
	private CouponStatus status;
	private String name;
	private int discountValue;
	private int quantity;
	private LocalDateTime expireAt;

	public Coupon(CouponType discountType, CouponStatus status, String name, int discountValue, int quantity,
		LocalDateTime expireAt) {
		this.discountType = discountType;
		this.status = status;
		this.name = name;
		this.discountValue = discountValue;
		this.quantity = quantity;
		this.expireAt = expireAt;
	}

	public static Coupon create(CouponType discountType, CouponStatus status, String name,
		int discountValue, int quantity, LocalDateTime expireAt) {
		validateDiscountValue(discountType, discountValue);
		return new Coupon(discountType, status, name, discountValue, quantity, expireAt);
	}

	public void validatePublishability(LocalDateTime nowDateTime) {
		validateStock();
		validateNotExpired(nowDateTime);
	}

	public void validateNotExpired(LocalDateTime nowDateTime) {
		if (this.expireAt.isBefore(nowDateTime)) {
			throw new RestApiException(CouponErrorCode.EXPIRED_COUPON);
		}
	}

	public void decreaseQuantity() {
		this.quantity--;
	}

	private void validateStock() {
		if (this.quantity < 1) {
			throw new RestApiException(CouponErrorCode.OUT_OF_STOCK_COUPON);
		}
	}

	private static void validateDiscountValue(CouponType discountType, int discountValue) {
		if (discountType == CouponType.PERCENT) {
			validatePercentDiscountValue(discountValue);
		}
	}

	private static void validatePercentDiscountValue(int discountValue) {
		if (discountValue > 100) {
			throw new RestApiException(CouponErrorCode.INVALID_PERCENT_DISCOUNT);
		}
	}
}