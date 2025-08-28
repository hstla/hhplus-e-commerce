package kr.hhplus.be.domain.order.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
	PENDING("주문 접수됨 (처리 대기중)"),
	AWAITING_PAYMENT("결제 대기중"),
	COMPLETED("주문 완료"),
	FAILED("주문 실패"),
	CANCELLED("주문 취소"),
	;

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}
}