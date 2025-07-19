package kr.hhplus.be.server.config.jpa.point.dto;

import jakarta.validation.constraints.NotNull;

public record PointResponse(
	Long userId,
	Long point
) {
}
