package kr.hhplus.be.server.config.jpa.order.infrastructure.order;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "user_coupon_id")
	private Long userCouponId;

	@Column(nullable = false)
	private Long totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private OrderStatus status;

	@Column(nullable = false)
	private LocalDateTime orderAt;

	public OrderEntity(Long userId, Long userCouponId, Long totalPrice, OrderStatus status, LocalDateTime orderAt) {
		this.userId = userId;
		this.userCouponId = userCouponId;
		this.totalPrice = totalPrice;
		this.status = status;
		this.orderAt = orderAt;
	}
}