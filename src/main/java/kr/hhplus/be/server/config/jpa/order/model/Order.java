package kr.hhplus.be.server.config.jpa.order.model;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {
	private Long id;
	private Long userId;
	private Long userCouponId;
	private Long totalPrice;
	private OrderStatus status;
	private LocalDateTime orderAt;

	public static Order create(Long userId, Long userCouponId, Long totalPrice, LocalDateTime orderAt) {
		return new Order(null, userId, userCouponId, totalPrice, OrderStatus.CREATED, orderAt);
	}

	public void markAsPaid() {
		this.status = OrderStatus.PAID;
	}
}