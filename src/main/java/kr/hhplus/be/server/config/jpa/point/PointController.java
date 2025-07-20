package kr.hhplus.be.server.config.jpa.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.point.dto.PointResponse;
import kr.hhplus.be.server.config.jpa.point.dto.ChargePointRequest;


@RestController
@RequestMapping("/api/points")
public class PointController implements PointApiSpec {

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<PointResponse> getPoint(Long userId) {
		return  ResponseEntity.ok(new PointResponse(1L, 1000L));
	}

	@Override
	@PostMapping("/{userId}")
	public ResponseEntity<PointResponse> chargePoint(Long userId, ChargePointRequest chargePoint) {
		return  ResponseEntity.ok(new PointResponse(1L, 1000L));
	}
}