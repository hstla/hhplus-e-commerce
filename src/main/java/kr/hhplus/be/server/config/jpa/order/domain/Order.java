package kr.hhplus.be.server.config.jpa.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.error.OrderErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id", unique = true)
	private Long id;
	private Long userId;
	private Long userCouponId;
	private Long totalPrice;
	private OrderStatus status;

	public Order(Long userId, Long userCouponId, Long totalPrice) {
		this.userId = userId;
		this.userCouponId = userCouponId;
		this.totalPrice = totalPrice;
		this.status = OrderStatus.CREATED;
	}

	public static Order createOrder(Long userId, Long userCouponId, long totalPrice) {
		return new Order(userId, userCouponId, totalPrice);
	}

	public void payValidate() {
		if (this.status.equals(OrderStatus.CREATED)) {
			throw new RestApiException(OrderErrorCode.CANNOT_PAY_NOT_CREATED_ORDER);
		}
	}

	public void markAsPaid() {
		this.status = OrderStatus.PAID;
	}
}