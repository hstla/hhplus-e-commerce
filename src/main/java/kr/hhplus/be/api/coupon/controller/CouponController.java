package kr.hhplus.be.api.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.api.coupon.controller.dto.CouponRequest;
import kr.hhplus.be.api.coupon.controller.dto.CouponResponse;
import kr.hhplus.be.api.coupon.usecase.CouponCreateUseCase;
import kr.hhplus.be.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponApiSpec {

	private final CouponCreateUseCase couponCreateUseCase;

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<CouponResponse.Coupon>> createCoupon(
		CouponRequest.Coupon createCouponRequest) {
		CouponResult.CouponDetail execute = couponCreateUseCase.execute(createCouponRequest.toCommand());
		return ResponseEntity.ok(CommonResponse.success(CouponResponse.Coupon.of(execute)));
	}
}