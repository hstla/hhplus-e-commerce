package kr.hhplus.be.domain.order.model;

import java.time.LocalDateTime;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "user_coupon_id")
	private Long userCouponId;
	@Column(name = "original_price", nullable = false)
	private Long originalPrice;
	@Column(name = "discount_price", nullable = false)
	private Long discountPrice;
	@Column(name = "total_price", nullable = false)
	private Long totalPrice;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private OrderStatus status;
	@Column(name = "order_at", nullable = false)
	private LocalDateTime orderAt;

	public static Order createPending(Long userId, Long userCouponId, LocalDateTime orderAt) {
		return Order.builder()
			.userId(userId)
			.userCouponId(userCouponId)
			.originalPrice(0L)
			.discountPrice(0L)
			.totalPrice(0L)
			.orderAt(orderAt)
			.status(OrderStatus.PENDING)
			.build();
	}

	public static Order create(Long userId, Long userCouponId, Long originalPrice, Long discountPrice, Long totalPrice, LocalDateTime orderAt) {
		return Order.builder()
			.userId(userId)
			.userCouponId(userCouponId)
			.originalPrice(originalPrice)
			.discountPrice(discountPrice)
			.totalPrice(totalPrice)
			.orderAt(orderAt)
			.status(OrderStatus.AWAITING_PAYMENT)
			.build();
	}

	public void markAsCompleted() {
		this.status = OrderStatus.COMPLETED;
	}

	public void markAsFailed() {
		this.status = OrderStatus.FAILED;
	}

	public boolean validatePayable() {
		return this.status == OrderStatus.AWAITING_PAYMENT;
	}

	public void markAsAwaitingPayment(Long originalPrice, Long discountPrice, long totalPrice) {
		this.originalPrice = originalPrice;
		this.discountPrice = discountPrice;
		this.totalPrice = totalPrice;
		this.status = OrderStatus.AWAITING_PAYMENT;
	}
}