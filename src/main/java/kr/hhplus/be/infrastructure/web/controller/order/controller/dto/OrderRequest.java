package kr.hhplus.be.infrastructure.web.controller.order.controller.dto;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.application.order.dto.OrderCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

	public record Order(
		@NotNull @PositiveOrZero Long userId,
		@Nullable @PositiveOrZero Long userCouponId,
		@NotEmpty List<@Valid OrderProduct> orderProductRequests
	) {
		public static Order of(Long userId, Long userCouponId, List<OrderProduct> orderItemRequests) {
			return new Order(userId, userCouponId, orderItemRequests);
		}

		public OrderCommand.Order toCommand() {
			List<OrderCommand.OrderProduct> orderProductCommand = orderProductRequests.stream()
				.map(op -> OrderCommand.OrderProduct.of(op.productOptionId(), op.quantity()))
				.toList();
			return OrderCommand.Order.of(userId, userCouponId, orderProductCommand);
		}
	}

	public record OrderProduct(
		@NotNull @PositiveOrZero Long productOptionId,
		@NotNull @Min(1) int quantity
	) {
		public static OrderProduct of(Long productOptionId, int quantity) {
			return new OrderProduct(productOptionId, quantity);
		}
	}
}