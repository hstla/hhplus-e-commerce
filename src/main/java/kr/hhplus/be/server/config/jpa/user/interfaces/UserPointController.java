package kr.hhplus.be.server.config.jpa.user.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.user.adapter.in.dto.point.PointResponse;
import kr.hhplus.be.server.config.jpa.user.adapter.in.dto.point.ChargePointRequest;
import kr.hhplus.be.server.config.jpa.user.application.port.in.FindUserUseCase;
import kr.hhplus.be.server.config.jpa.user.application.port.in.UpdateUserUseCase;
import kr.hhplus.be.server.config.jpa.user.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserPointController implements UserPointApiSpec {

	private final FindUserUseCase findUserPointUseCase;
	private final UpdateUserUseCase updateUserUseCase;

	@Override
	@GetMapping("/{userId}/points")
	public ResponseEntity<CommonResponse<PointResponse>> getPoint(Long userId) {
		User findUser = findUserPointUseCase.findUser(userId);
		return ResponseEntity.ok(CommonResponse.success(new PointResponse(findUser.getId(), findUser.getPointAmount())));
	}

	@Override
	@PostMapping("/{userId}/points")
	public ResponseEntity<CommonResponse<PointResponse>> chargePoint(Long userId, ChargePointRequest chargePoint) {
		User updateUserPoint = updateUserUseCase.chargeUserPoint(userId, chargePoint.chargePoint());
		return  ResponseEntity.ok(CommonResponse.success(new PointResponse(updateUserPoint.getId(), updateUserPoint.getPointAmount())));
	}
}