package kr.hhplus.be.server.config.jpa.product.service;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductInput {

	@Getter
	@NoArgsConstructor
	public static class order {
		private Long productOptionId;
		private int quantity;

		private order(Long productOptionId, int quantity) {
			this.productOptionId = productOptionId;
			this.quantity = quantity;
		}

		public static order of(Long productOptionId, int quantity) {
			return new order(productOptionId, quantity);
		}
	}
}
