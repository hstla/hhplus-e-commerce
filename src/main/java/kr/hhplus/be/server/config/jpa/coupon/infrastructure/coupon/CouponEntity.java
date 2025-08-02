package kr.hhplus.be.server.config.jpa.coupon.infrastructure.coupon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id", unique = true)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "discount_type", nullable = false)
	private CouponType discountType;

	@Column(name = "name", length = 30, nullable = false)
	private String name;

	@Column(name = "discount_value", nullable = false)
	private Long discountValue;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "expire_at", nullable = false)
	private LocalDateTime expireAt;

	public CouponEntity(CouponType discountType, String name,
		Long discountValue, int quantity, LocalDateTime expireAt) {
		this(null, discountType, name, discountValue, quantity, expireAt);
	}
}