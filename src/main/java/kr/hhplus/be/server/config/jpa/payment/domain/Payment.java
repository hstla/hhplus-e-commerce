package kr.hhplus.be.server.config.jpa.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id", unique = true)
	private Long id;
	private Long orderId;
	private Long userId;
	private Long paymentAmount;
	private PaymentStatus status;

	public Payment(Long orderId, Long userId, Long paymentAmount) {
		this.orderId = orderId;
		this.userId = userId;
		this.paymentAmount = paymentAmount;
		this.status = PaymentStatus.COMPLETED;
	}

	public static Payment createPayment(Long orderId, Long userId, Long paymentAmount) {
		return new Payment(orderId, userId, paymentAmount);
	}


}