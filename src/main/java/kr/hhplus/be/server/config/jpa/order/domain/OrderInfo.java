package kr.hhplus.be.server.config.jpa.order.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInfo {

	@Getter
	@RequiredArgsConstructor
	public static class Info {
		private Long id;
		private Long userId;
		private Long userCouponId;
		private Long totalPrice;
		private OrderStatus status;

		private Info(Long id, Long userId, Long userCouponId, Long totalPrice, OrderStatus status) {
			this.id = id;
			this.userId = userId;
			this.userCouponId = userCouponId;
			this.totalPrice = totalPrice;
			this.status = status;
		}

		public static Info of(Order order) {
			return new Info(order.getId(), order.getUserId(), order.getUserCouponId(), order.getTotalPrice(), order.getStatus());
		}
	}
}