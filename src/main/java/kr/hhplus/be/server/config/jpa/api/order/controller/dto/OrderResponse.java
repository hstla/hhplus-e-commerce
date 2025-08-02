package kr.hhplus.be.server.config.jpa.api.order.controller.dto;

import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {

	@Getter
	@NoArgsConstructor
	public static class Order {
		private Long id;
		private Long totalPrice;
		private OrderStatus status;

		private Order(Long id, Long totalPrice, OrderStatus status) {
			this.id = id;
			this.totalPrice = totalPrice;
			this.status = status;
		}

		public static Order of(OrderResult.Order orderResult) {
			return new Order(orderResult.getId(), orderResult.getTotalPrice(), orderResult.getStatus());
		}
	}
}
