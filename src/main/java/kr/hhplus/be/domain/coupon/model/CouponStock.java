package kr.hhplus.be.domain.coupon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
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
	@Column(name = "id")
	private Long id;
	@Column(name = "coupon_id", nullable = false)
	private Long couponId;
	@Column(name = "stock", nullable = false)
	private int stock;

	public static CouponStock create(Long couponId, int stock) {
		return new CouponStock(null, couponId, stock);
	}

	public boolean decreaseStock() {
		checkRemainStock();
		this.stock -= 1;
		return true;
	}

	private void checkRemainStock() {
		if (this.stock < 1) {
			throw new RestApiException(CouponErrorCode.OUT_OF_STOCK_COUPON);
		}
	}
}