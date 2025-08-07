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
public class UserCoupon {
	private Long id;
	private Long userId;
	private Long couponId;
	private UserCouponStatus status;
	private LocalDateTime issuedAt;
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
}