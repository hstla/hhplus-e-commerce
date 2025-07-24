package kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
	name = "user_coupon",
	uniqueConstraints = {
		// 한 유저는 한 쿠폰을 여러번 발급받는 것은 불가능합니다.
		@UniqueConstraint(columnNames = {"couponId", "userId"})
	}
)
public class UserCoupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_coupon_id", unique = true)
	private Long id;
	private Long couponId;
	private Long userId;
	private LocalDateTime publishAt;
	private LocalDateTime usedAt;

	public UserCoupon(Long couponId, Long userId, LocalDateTime publishAt, LocalDateTime usedAt) {
		this.couponId = couponId;
		this.userId = userId;
		this.publishAt = publishAt;
		this.usedAt = usedAt;
	}

	public static UserCoupon create(Long userId, Long couponId) {
		return new UserCoupon(couponId, userId, LocalDateTime.now(), null);
	}

	public void used() {
		useValidate();
		this.usedAt = LocalDateTime.now();
	}

	private void useValidate() {
		if (usedAt != null) {
			throw new RestApiException(CouponErrorCode.ALREADY_USED_COUPON);
		}
	}
}