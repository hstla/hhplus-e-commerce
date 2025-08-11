package kr.hhplus.be.server.config.jpa.coupon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "coupon_stock")
public class CouponStock {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_stock_id")
	private Long id;
	@Column(name = "coupon_id", nullable = false)
	private Long couponId;
	@Column(name = "stock", nullable = false)
	private int stock;

	public static CouponStock create(Long couponId, int stock) {
		return new CouponStock(null, couponId, stock);
	}

	public boolean decreaseStock() {
		validateStock();
		this.stock -= 1;
		return true;
	}

	private void validateStock() {
		if (this.stock < 1) {
			throw new RestApiException(CouponErrorCode.OUT_OF_STOCK_COUPON);
		}
	}
}