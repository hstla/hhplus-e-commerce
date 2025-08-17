package kr.hhplus.be.domain.payment.model;

import lombok.Getter;

@Getter
public enum PaymentType {
	POINT("포인트 결제"),
	CARD("카드 결제"),
	;

	private final String description;

	PaymentType(String description) {
		this.description = description;
	}
}