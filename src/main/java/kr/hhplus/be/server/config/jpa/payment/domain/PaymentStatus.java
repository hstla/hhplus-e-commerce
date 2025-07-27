package kr.hhplus.be.server.config.jpa.payment.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {
	PENDING("결제 대기 중 (PG사와 통신 중)"),
	COMPLETED("결제 완료"),
	FAILED("결제 실패"),
	CANCELLED("결제 취소 (사용자 또는 관리자)"),
	REFUNDED("환불 완료");

	private final String description;

	PaymentStatus(String description) {
		this.description = description;
	}
}
