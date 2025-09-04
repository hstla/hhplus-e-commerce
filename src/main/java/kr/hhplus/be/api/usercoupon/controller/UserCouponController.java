package kr.hhplus.be.api.usercoupon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.api.usercoupon.controller.dto.UserCouponRequest;
import kr.hhplus.be.api.usercoupon.controller.dto.UserCouponResponse;
import kr.hhplus.be.api.usercoupon.usecase.FindUserCouponUseCase;
import kr.hhplus.be.api.usercoupon.usecase.IssueCouponUseCase;
import kr.hhplus.be.api.usercoupon.usecase.dto.UserCouponResult;
import kr.hhplus.be.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class UserCouponController implements CouponApiSpec{

	private final IssueCouponUseCase issueCouponUseCase;
	private final FindUserCouponUseCase findUserCouponUseCase;

	@Override
	@PostMapping("/publish")
	public ResponseEntity<Void> publishCoupon(UserCouponRequest.Publish couponPublishRequest) {
		issueCouponUseCase.execute(couponPublishRequest.toCommand());
		return ResponseEntity.ok().build();
	}

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserCouponResponse.UserCoupons>> getUserCoupon(Long userId) {
		List<UserCouponResult.UserCouponInfo> userCoupons = findUserCouponUseCase.execute(userId);
		return ResponseEntity.ok(CommonResponse.success(UserCouponResponse.UserCoupons.of(userId, userCoupons)));
	}
}