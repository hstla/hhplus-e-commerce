package kr.hhplus.be.server.config.jpa.order.domain;

import java.util.List;

import kr.hhplus.be.server.config.jpa.order.application.OrderCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInput {

	@Getter
	@RequiredArgsConstructor
	public static class Create {
		private Long userId;
		private Long userCouponId;
		private List<OrderProduct> orderItemRequests;

		private Create(Long userId, Long userCouponId, List<OrderProduct> orderItemRequests) {
			this.userId = userId;
			this.userCouponId = userCouponId;
			this.orderItemRequests = orderItemRequests;
		}

		public static Create of(Long userId, Long userCouponId, List<OrderProduct> orderItemRequests) {
			return new  Create(userId, userCouponId, orderItemRequests);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class OrderProduct {
		private Long productOptionId;
		private int quantity;

		private OrderProduct(Long productOptionId, int quantity) {
			this.productOptionId = productOptionId;
			this.quantity = quantity;
		}

		public static OrderProduct of(Long productOptionId, int quantity) {
			return new OrderProduct(productOptionId, quantity);
		}
	}
}