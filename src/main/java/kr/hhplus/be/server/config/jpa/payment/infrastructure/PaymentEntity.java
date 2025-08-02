package kr.hhplus.be.server.config.jpa.payment.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id", unique = true)
	private Long id;
	@Column(name = "order_id", nullable = false)
	private Long orderId;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	private Long paymentAmount;
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	public PaymentEntity(Long orderId, Long userId, Long paymentAmount, PaymentStatus status) {
		this.orderId = orderId;
		this.userId = userId;
		this.paymentAmount = paymentAmount;
		this.status = status;
	}
}