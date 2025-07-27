package kr.hhplus.be.server.config.jpa.order.interfaces.dto;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.order.application.OrderCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

	@Getter
	@NoArgsConstructor
	public static class Order {
		@NotNull @PositiveOrZero private Long userId;
		@Nullable @PositiveOrZero private Long userCouponId;
		@NotEmpty private List<@Valid OrderProduct> orderProductRequests;

		private Order(Long userId, @Nullable Long userCouponId, List<@Valid OrderProduct> orderItemRequests) {
			this.userId = userId;
			this.userCouponId = userCouponId;
			this.orderProductRequests = orderItemRequests;
		}

		private static Order of(Long userId, Long userCouponId, List<OrderProduct> orderItemRequests) {
			return new Order(userId, userCouponId, orderItemRequests);
		}

		public OrderCommand.Order toCommand() {
			List<OrderCommand.OrderProduct> orderProductCommand = orderProductRequests.stream().
				map(op -> OrderCommand.OrderProduct.of(op.getProductOptionId(), op.getQuantity())).toList();
			return OrderCommand.Order.of(userId, userCouponId, orderProductCommand);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class OrderProduct {
		@NotNull @PositiveOrZero private Long productOptionId;
		@NotNull @Min(1) private int quantity;

		private OrderProduct(Long productOptionId, int quantity) {
			this.productOptionId = productOptionId;
			this.quantity = quantity;
		}

		public static OrderProduct of(Long productOptionId, int quantity) {
			return new OrderProduct(productOptionId, quantity);
		}
	}
}