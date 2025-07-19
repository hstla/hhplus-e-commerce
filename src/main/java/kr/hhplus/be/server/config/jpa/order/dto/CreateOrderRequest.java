package kr.hhplus.be.server.config.jpa.order.dto;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateOrderRequest(
	@NotNull @PositiveOrZero Long userId,
	@Nullable @PositiveOrZero Long couponId, // 쿠폰을 사용하지 않으면 null 가능
	@NotEmpty List<@Valid OrderItemRequest> orderItemRequests
) {
}