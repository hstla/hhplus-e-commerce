package kr.hhplus.be.server.config.jpa.order.service;

import java.util.List;

import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
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

	public record PreOrderInfo(
		Long totalAmount,
		List<OrderProduct> orderProducts
	) {
		public static PreOrderInfo of(Long totalAmount, List<OrderProduct> orderProducts) {
			return new PreOrderInfo(totalAmount, orderProducts);
		}
	}
}