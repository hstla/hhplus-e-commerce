package kr.hhplus.be.server.config.jpa.order.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderProductInfo {

	@Getter
	@RequiredArgsConstructor
	public static class Info {
		private Long id;
		private Long orderId;
		private Long productOptionId;
		private int quantity;
		private int unitPrice;

		private Info(Long id, Long orderId, Long productOptionId, int quantity, int unitPrice) {
			this.id = id;
			this.orderId = orderId;
			this.productOptionId = productOptionId;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
		}

		public static Info of(OrderProduct  orderProduct) {
			return new Info(orderProduct.getId(), orderProduct.getOrderId(), orderProduct.getProductOptionId(), orderProduct.getQuantity(), orderProduct.getUnitPrice());
		}
	}
}
