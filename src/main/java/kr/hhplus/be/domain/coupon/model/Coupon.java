package kr.hhplus.be.domain.coupon.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.global.common.BaseEntity;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon")
public class Coupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name", length = 30, nullable = false)
	private String name;
	@Enumerated(EnumType.STRING)
	@Column(name = "discount_type", nullable = false)
	private CouponType discountType;
	@Column(name = "discount_value", nullable = false)
	private Long discountValue;
	@Column(name = "initial_stock", nullable = false)
	private int initialStock;
	@Column(name = "expire_at", nullable = false)
	private LocalDateTime expireAt;

	public static Coupon create(String name, CouponType discountType,
		Long discountValue, int initialStock, LocalDateTime expireAt) {
		validateDiscountValue(discountType, discountValue);
		return new Coupon(null, name, discountType, discountValue, initialStock, expireAt);
	}

	public void validateNotExpired(LocalDateTime nowDateTime) {
		if (this.expireAt.isBefore(nowDateTime)) {
			throw new RestApiException(CouponErrorCode.EXPIRED_COUPON);
		}
	}

	private static void validateDiscountValue(CouponType discountType, Long discountValue) {
		if (discountType == CouponType.PERCENT) {
			validatePercentDiscountValue(discountValue);
		} else if (discountValue <= 0) {
			throw new RestApiException(CouponErrorCode.INVALID_FIXED_DISCOUNT);
		}
	}

	private static void validatePercentDiscountValue(Long discountValue) {
		if (discountValue <= 0 || discountValue > 100) {
			throw new RestApiException(CouponErrorCode.INVALID_PERCENT_DISCOUNT);
		}
	}
}