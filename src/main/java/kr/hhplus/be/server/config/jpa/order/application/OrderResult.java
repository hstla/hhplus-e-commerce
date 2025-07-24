package kr.hhplus.be.server.config.jpa.order.application;

import org.aspectj.weaver.ast.Or;

import kr.hhplus.be.server.config.jpa.order.domain.OrderInfo;
import kr.hhplus.be.server.config.jpa.order.domain.OrderStatus;
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

		public static Order of(OrderInfo.Info orderResultInfo) {
			return new Order(orderResultInfo.getId(), orderResultInfo.getUserId(),
				orderResultInfo.getUserCouponId(), orderResultInfo.getTotalPrice(), orderResultInfo.getStatus());
		}
	}
}