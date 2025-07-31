package kr.hhplus.be.server.config.jpa.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.api.user.controller.dto.point.UserPointRequest;
import kr.hhplus.be.server.config.jpa.api.user.controller.dto.point.UserPointResponse;
import kr.hhplus.be.server.config.jpa.api.user.usecase.ChargeUserPointUseCase;
import kr.hhplus.be.server.config.jpa.api.user.usecase.FindUserPointUseCase;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserPointResult;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserPointController implements UserPointApiSpec {

	private final FindUserPointUseCase findUserPointUseCase;
	private final ChargeUserPointUseCase chargeUserPointUseCase;

	@Override
	@GetMapping("/{userId}/points")
	public ResponseEntity<CommonResponse<UserPointResponse.UserPoint>> getPoint(Long userId) {
		UserPointResult.UserPoint userPoint = findUserPointUseCase.execute(userId);
		return ResponseEntity.ok(CommonResponse.success(UserPointResponse.UserPoint.of(userPoint)));
	}

	@Override
	@PostMapping("/{userId}/points")
	public ResponseEntity<CommonResponse<UserPointResponse.UserPoint>> chargePoint(Long userId, UserPointRequest.ChargePoint chargePoint) {
		UserPointResult.UserPoint userPoint = chargeUserPointUseCase.execute(userId, chargePoint.getChargePoint());
		return ResponseEntity.ok(CommonResponse.success(UserPointResponse.UserPoint.of(userPoint)));
	}
}