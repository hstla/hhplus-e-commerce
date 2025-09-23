package kr.hhplus.be.infrastructure.web.controller.usercoupon.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kr.hhplus.be.application.usercoupon.dto.UserCouponResult;
import kr.hhplus.be.application.usercoupon.usecase.FindUserCouponUseCase;
import kr.hhplus.be.application.usercoupon.usecase.GetCouponIssuanceStatusUseCase;
import kr.hhplus.be.application.usercoupon.usecase.IssueCouponUseCase;
import kr.hhplus.be.global.common.CommonResponse;
import kr.hhplus.be.infrastructure.sse.SseEmitterService;
import kr.hhplus.be.infrastructure.web.controller.usercoupon.controller.dto.UserCouponRequest;
import kr.hhplus.be.infrastructure.web.controller.usercoupon.controller.dto.UserCouponResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class UserCouponController implements CouponApiSpec {

	private final IssueCouponUseCase issueCouponUseCase;
	private final FindUserCouponUseCase findUserCouponUseCase;
	private final GetCouponIssuanceStatusUseCase getCouponIssuanceStatusUseCase;
	private final SseEmitterService sseEmitterService;

	@Override
	@PostMapping("/publish")
	public ResponseEntity<CommonResponse<UserCouponResponse.publish>> publishCoupon(UserCouponRequest.Publish couponPublishRequest) {
		String taskId = issueCouponUseCase.execute(couponPublishRequest.toCommand());
		return ResponseEntity.accepted().body(CommonResponse.success(new UserCouponResponse.publish(taskId)));
	}

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserCouponResponse.UserCoupons>> getUserCoupon(Long userId) {
		List<UserCouponResult.UserCouponInfo> userCoupons = findUserCouponUseCase.execute(userId);
		return ResponseEntity.ok(CommonResponse.success(UserCouponResponse.UserCoupons.of(userId, userCoupons)));
	}

	@Override
	@GetMapping("/publish/status/{taskId}")
	public SseEmitter getIssuanceStatus(String taskId) {
		Optional<UserCouponResult.UserCouponConfirmation> statusOptional = getCouponIssuanceStatusUseCase.execute(taskId);

		if (statusOptional.isPresent()) {
			SseEmitter emitter = new SseEmitter();
			try {
				emitter.send(SseEmitter.event().name("coupon-status").data(statusOptional.get()));
				emitter.complete();
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
			return emitter;
		} else {
			return sseEmitterService.addEmitter(taskId);
		}
	}
}