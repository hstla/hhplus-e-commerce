package kr.hhplus.be.server.config.jpa.point.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ChargePointRequest(
	@NotNull @PositiveOrZero Long chargePoint
) {
}
