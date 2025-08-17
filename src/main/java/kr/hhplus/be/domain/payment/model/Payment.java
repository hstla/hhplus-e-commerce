package kr.hhplus.be.domain.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "order_id", nullable = false)
	private Long orderId;
	@Column(name = "payment_amount", nullable = false)
	private Long paymentAmount;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PaymentStatus status;

	private Payment(Long orderId, Long paymentAmount, PaymentStatus status) {
		this.orderId = orderId;
		this.paymentAmount = paymentAmount;
		this.status = status;
	}

	public static Payment create(Long orderId, Long paymentAmount) {
		return new Payment(orderId, paymentAmount, PaymentStatus.COMPLETED);
	}
}