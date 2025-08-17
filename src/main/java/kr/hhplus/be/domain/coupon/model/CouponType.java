package kr.hhplus.be.domain.coupon.model;

import lombok.Getter;

@Getter
public enum CouponType {

	FIXED("고정 금액 할인 쿠폰"),
	PERCENT("퍼센트(%) 할인 쿠폰");

	private final String description;

	CouponType(String description) {
		this.description = description;
	}
}