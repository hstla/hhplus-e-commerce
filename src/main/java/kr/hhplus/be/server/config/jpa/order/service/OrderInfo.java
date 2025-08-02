package kr.hhplus.be.server.config.jpa.order.service;

import java.util.List;

import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderInfo {

	@Getter
	@NoArgsConstructor
	public static class Info {
		private Long id;
		private Long totalPrice;
		private OrderStatus status;
		private Long userId;
		private Long userCouponId;

		private Info(Long id, Long totalPrice, OrderStatus status, Long userId, Long userCouponId) {
			this.id = id;
			this.totalPrice = totalPrice;
			this.status = status;
			this.userId = userId;
			this.userCouponId = userCouponId;
		}

		public static Info of(Order order) {
			return new Info(order.getId(), order.getTotalPrice(), order.getStatus(), order.getUserId(), order.getUserCouponId());
		}
	}

	@Getter
	@NoArgsConstructor
	public static class PreOrderInfo {
		private Long totalAmount;
		private List<OrderProduct> orderProducts;

		private PreOrderInfo(Long totalAmount, List<OrderProduct> orderProducts) {
			this.totalAmount = totalAmount;
			this.orderProducts = orderProducts;
		}

		public static PreOrderInfo of(Long totalAmount, List<OrderProduct> orderProducts) {
			return new PreOrderInfo(totalAmount, orderProducts);
		}
	}
}