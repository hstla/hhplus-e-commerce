package kr.hhplus.be.server.config.jpa.api.order.usecase.dto;

import java.util.List;

import kr.hhplus.be.server.config.jpa.product.service.ProductInput;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

	@Getter
	@NoArgsConstructor
	public static class Order {
		private Long userId;
		private Long userCouponId;
		private List<OrderProduct> orderItemRequests;

		private Order(Long userId, Long userCouponId, List<OrderProduct> orderItemRequests) {
			this.userId = userId;
			this.userCouponId = userCouponId;
			this.orderItemRequests = orderItemRequests;
		}

		public static Order of(Long userId, Long userCouponId, List<OrderProduct> orderProductCommand) {
			return new Order(userId, userCouponId, orderProductCommand);
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

		public ProductInput.order toInput() {
			return ProductInput.order.of(this.getProductOptionId(), this.getQuantity());
		}
	}
}