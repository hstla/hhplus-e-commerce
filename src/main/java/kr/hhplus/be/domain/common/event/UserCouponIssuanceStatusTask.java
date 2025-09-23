package kr.hhplus.be.domain.common.event;

import java.time.LocalDateTime;

public record UserCouponIssuanceStatusTask (
	String status,
	String message,
	LocalDateTime issuedAt
) {
	public static UserCouponIssuanceStatusTask of(String status, String message, LocalDateTime issuedAt) {
		return new UserCouponIssuanceStatusTask(status, message, issuedAt);
	}
}
