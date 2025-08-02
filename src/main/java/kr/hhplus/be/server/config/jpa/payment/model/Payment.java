package kr.hhplus.be.server.config.jpa.payment.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {
	private Long id;
	private Long orderId;
	private Long userId;
	private Long paymentAmount;
	private PaymentStatus status;

	public Payment(Long orderId, Long userId, Long paymentAmount, PaymentStatus status) {
		this.orderId = orderId;
		this.userId = userId;
		this.paymentAmount = paymentAmount;
		this.status = status;
	}

	public static Payment create(Long orderId, Long userId, Long paymentAmount) {
		return new Payment(orderId, userId, paymentAmount, PaymentStatus.COMPLETED);
	}
}