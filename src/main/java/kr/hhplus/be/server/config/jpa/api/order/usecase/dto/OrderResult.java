package kr.hhplus.be.server.config.jpa.api.order.usecase.dto;

import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import kr.hhplus.be.server.config.jpa.order.service.OrderInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResult {

	@Getter
	@NoArgsConstructor
	public static class Order {
		private Long id;
		private Long userId;
		private Long userCouponId;
		private Long totalPrice;
		private OrderStatus status;

		private Order(Long id, Long userId, Long userCouponId, Long totalPrice, OrderStatus status) {
			this.id = id;
			this.userId = userId;
			this.userCouponId = userCouponId;
			this.totalPrice = totalPrice;
			this.status = status;
		}

		public static Order of(OrderInfo.Info info) {
			return new Order(info.getId(), info.getUserId(),
				info.getUserCouponId(), info.getTotalPrice(), info.getStatus());
		}
	}
}