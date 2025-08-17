package kr.hhplus.be.api.order.controller.dto;

import kr.hhplus.be.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.domain.order.model.OrderStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {

	public record Order(Long id, Long totalPrice, OrderStatus status) {

		public static Order of(OrderResult.Order orderResult) {
			return new Order(orderResult.id(), orderResult.totalPrice(), orderResult.status());
		}
	}
}
