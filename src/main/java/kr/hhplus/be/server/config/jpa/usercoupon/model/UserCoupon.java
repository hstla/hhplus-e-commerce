package kr.hhplus.be.server.config.jpa.usercoupon.model;

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
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserCoupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
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

	public static UserCoupon publish(Long userId, Long couponId, LocalDateTime issuedAt) {
		return new UserCoupon(
			null,
			userId,
			couponId,
			UserCouponStatus.ISSUED,
			issuedAt,
			null
		);
	}

	public void use(LocalDateTime useDateTime) {
		validateUsability();
		this.status = UserCouponStatus.USED;
		this.usedAt = useDateTime;
	}

	public void validateUsability() {
		if (this.status != UserCouponStatus.ISSUED) {
			throw new RestApiException(CouponErrorCode.INVALID_COUPON_TYPE);
		}
		if (this.usedAt != null) {
			throw new RestApiException(CouponErrorCode.ALREADY_USED_COUPON);
		}
	}

	public boolean validateOwnerShip(Long inputUserId) {
		if (!userId.equals(inputUserId)) {
			throw new RestApiException(CouponErrorCode.INVALID_COUPON_OWNERSHIP);
		}
		return true;
	}
}