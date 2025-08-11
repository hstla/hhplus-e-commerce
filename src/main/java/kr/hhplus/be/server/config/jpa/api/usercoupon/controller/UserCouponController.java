package kr.hhplus.be.server.config.jpa.api.usercoupon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.api.usercoupon.controller.dto.CouponResponse;
import kr.hhplus.be.server.config.jpa.api.usercoupon.controller.dto.UserCouponRequest;
import kr.hhplus.be.server.config.jpa.api.usercoupon.controller.dto.UserCouponResponse;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.FindUserCouponUseCase;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.PublishCouponUseCase;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class UserCouponController implements CouponApiSpec{

	private final PublishCouponUseCase publishCouponUseCase;
	private final FindUserCouponUseCase findUserCouponUseCase;

	@Override
	@PostMapping("/publish")
	public ResponseEntity<CommonResponse<CouponResponse.Coupon>> publishCoupon(UserCouponRequest.Publish couponPublishRequest) {
		CouponResult.CouponInfo publish = publishCouponUseCase.execute(couponPublishRequest.toCommand());
		return ResponseEntity.ok(CommonResponse.success(CouponResponse.Coupon.of(publish)));
	}

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserCouponResponse.UserCoupons>> getUserCoupon(Long userId) {
		List<CouponResult.UserCouponInfo> userCoupons = findUserCouponUseCase.execute(userId);
		return ResponseEntity.ok(CommonResponse.success(UserCouponResponse.UserCoupons.of(userId, userCoupons)));
	}
}