package kr.hhplus.be.server.config.jpa.api.order.usecase.dto;

import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import kr.hhplus.be.server.config.jpa.order.service.OrderInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResult {

	public record Order(
		Long id,
		Long userId,
		Long userCouponId,
		Long totalPrice,
		OrderStatus status
	) {
		public static Order of(OrderInfo.OrderDetail info) {
			return new Order(info.id(), info.userId(),
				info.userCouponId(), info.totalPrice(), info.status());
		}
	}
}