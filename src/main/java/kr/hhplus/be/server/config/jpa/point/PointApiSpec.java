package kr.hhplus.be.server.config.jpa.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.point.dto.ChargePointRequest;
import kr.hhplus.be.server.config.jpa.point.dto.PointResponse;

@Tag(name="포인트", description = "포인트 관련 API")
public interface PointApiSpec {

	@Operation(summary = "유저 포인트 조회", description = "유저 아이디를 입력받아 현재 포인트를 반환합니다.")
	ResponseEntity<PointResponse> getPoint(
		@Parameter(description = "사용자 아이디", required = true) @PathVariable @PositiveOrZero Long userId
	);

	@Operation(summary = "유저 포인트 충전", description = "유저 아이디와 포인트를 입력받아 포인트 충전 후 총 포인트를 반환합니다.")
	ResponseEntity<PointResponse> chargePoint(
		@Parameter(description = "사용자 아이디", required = true) @PathVariable @PositiveOrZero Long userId,
		@Parameter(description = "충전할 포인트", required = true) @RequestBody @Valid ChargePointRequest chargePoint
	);
}