package kr.hhplus.be.domain.order.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
	CREATED("주문 생성됨(결제 전)"),
	PAID("결제 완료"),
	CANCELLED("주문 취소"),
	FAILED("결제 실패"),
	REFUNDED("환불 완료");

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}
}