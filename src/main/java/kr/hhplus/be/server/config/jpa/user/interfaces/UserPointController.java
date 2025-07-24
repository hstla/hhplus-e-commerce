package kr.hhplus.be.server.config.jpa.user.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.user.application.UserFacade;
import kr.hhplus.be.server.config.jpa.user.application.UserResult;
import kr.hhplus.be.server.config.jpa.user.interfaces.dto.point.UserPointRequest;
import kr.hhplus.be.server.config.jpa.user.interfaces.dto.point.UserPointResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserPointController implements UserPointApiSpec {

	private final UserFacade userFacade;

	@Override
	@GetMapping("/{userId}/points")
	public ResponseEntity<CommonResponse<UserPointResponse.UserPoint>> getPoint(Long userId) {
		UserResult.UserPoint findUserPoint = userFacade.findUserPoint(userId);
		return ResponseEntity.ok(CommonResponse.success(UserPointResponse.UserPoint.of(findUserPoint.getId(), findUserPoint.getPoint())));
	}

	@Override
	@PostMapping("/{userId}/points")
	public ResponseEntity<CommonResponse<UserPointResponse.UserPoint>> chargePoint(Long userId, UserPointRequest.ChargePoint chargePoint) {
		UserResult.UserPoint chargeUserPoint = userFacade.chargeUserPoint(userId, chargePoint.getChargePoint());
		return  ResponseEntity.ok(CommonResponse.success(UserPointResponse.UserPoint.of(chargeUserPoint.getId(), chargeUserPoint.getPoint())));
	}
}