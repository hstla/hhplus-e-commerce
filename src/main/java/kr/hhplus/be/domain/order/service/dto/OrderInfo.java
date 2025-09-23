package kr.hhplus.be.domain.order.service.dto;

import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderStatus;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderInfo {

	public record OrderDetail(
		Long id,
		Long totalPrice,
		OrderStatus status,
		Long userId,
		Long userCouponId
	) {
		public static OrderDetail of(Order order) {
			return new OrderDetail(order.getId(), order.getTotalPrice(), order.getStatus(), order.getUserId(), order.getUserCouponId());
		}
	}
}