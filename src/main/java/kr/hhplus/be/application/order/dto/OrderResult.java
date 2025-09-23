package kr.hhplus.be.application.order.dto;

import kr.hhplus.be.domain.order.model.OrderStatus;
import kr.hhplus.be.domain.order.service.dto.OrderInfo;
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