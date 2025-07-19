package kr.hhplus.be.server.config.jpa.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderItemRequest(
	@NotNull @PositiveOrZero Long productId,
	@NotNull @PositiveOrZero Long productOptionId,
	@NotNull @Min(1) int quantity
) {

}
