package kr.hhplus.be.api.order.usecase.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

	public record Order(
		Long userId,
		Long userCouponId,
		List<OrderProduct> orderItemRequests
	) {
		public static Order of(Long userId, Long userCouponId, List<OrderProduct> orderProductCommand) {
			return new Order(userId, userCouponId, orderProductCommand);
		}
	}

	public record OrderProduct(
		Long productOptionId,
		int quantity
	) {
		public static OrderProduct of(Long productOptionId, int quantity) {
			return new OrderProduct(productOptionId, quantity);
		}
	}
}