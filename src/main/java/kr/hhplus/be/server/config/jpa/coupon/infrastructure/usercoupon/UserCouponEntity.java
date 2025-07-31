package kr.hhplus.be.server.config.jpa.coupon.infrastructure.usercoupon;

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
import kr.hhplus.be.server.config.jpa.coupon.model.UserCouponStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserCouponEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_coupon_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "coupon_id", nullable = false)
	private Long couponId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private UserCouponStatus status;

	@Column(name = "issued_at", nullable = false)
	private LocalDateTime issuedAt;

	@Column(name = "used_at")
	private LocalDateTime usedAt;

	public UserCouponEntity(Long userId, Long couponId, UserCouponStatus status,
		LocalDateTime issuedAt, LocalDateTime usedAt) {
		this(null, userId, couponId, status, issuedAt, usedAt);
	}
}