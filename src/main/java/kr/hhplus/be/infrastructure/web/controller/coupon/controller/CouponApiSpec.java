package kr.hhplus.be.infrastructure.web.controller.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.global.common.CommonResponse;
import kr.hhplus.be.infrastructure.web.controller.coupon.controller.dto.CouponRequest;
import kr.hhplus.be.infrastructure.web.controller.coupon.controller.dto.CouponResponse;

@Tag(name="쿠폰", description = "쿠폰 관련 API")
public interface CouponApiSpec {

	@Operation(summary = "쿠폰 생성", description = "쿠폰 정보를 받아 쿠폰을 생성합니다.")
	ResponseEntity<CommonResponse<CouponResponse.Coupon>> createCoupon(
		@Parameter(description = "생성할 쿠폰 정보", required = true)
		@RequestBody @Valid CouponRequest.Coupon createCouponRequest
	);
}