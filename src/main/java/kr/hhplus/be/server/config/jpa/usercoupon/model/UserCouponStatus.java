package kr.hhplus.be.server.config.jpa.usercoupon.model;

import lombok.Getter;

@Getter
public enum UserCouponStatus {
	ISSUED("발급됨"),
	USED("사용됨"),
	EXPIRED("만료됨"),
	;

	private final String description;

	UserCouponStatus(String description) {
		this.description = description;
	}
}