package kr.hhplus.be.server.config.jpa.api.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.api.coupon.controller.dto.CouponResponse;
import kr.hhplus.be.server.config.jpa.api.coupon.controller.dto.UserCouponRequest;
import kr.hhplus.be.server.config.jpa.api.coupon.controller.dto.UserCouponResponse;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;

@Tag(name="쿠폰", description = "쿠폰 관련 API")
public interface CouponApiSpec {

	@Operation(summary = "선착순 쿠폰 발급", description = "선착순으로 쿠폰발급을 하고 해당 쿠폰을 반환합니다.")
	ResponseEntity<CommonResponse<CouponResponse.Coupon>> publishCoupon(
		@Parameter(description = "사용자, 쿠폰 아이디", required = true) @RequestBody @Valid UserCouponRequest.Publish couponPublishRequest
	);

	@Operation(summary = "자신의 쿠폰 조회", description = "유저 아이디를 입력받아 유저의 쿠폰을 조회합니다.")
	ResponseEntity<CommonResponse<UserCouponResponse.UserCoupons>> getUserCoupon(
		@Parameter(description = "사용자 아이디", required = true) @PathVariable @PositiveOrZero Long userId
	);
}