package kr.hhplus.be.server.config.jpa.coupon.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.coupon.application.CouponFacade;
import kr.hhplus.be.server.config.jpa.coupon.application.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.interfaces.dto.CouponRequest;
import kr.hhplus.be.server.config.jpa.coupon.interfaces.dto.CouponResponse;
import kr.hhplus.be.server.config.jpa.coupon.interfaces.dto.UserCouponResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class UserCouponController implements CouponApiSpec{

	private final CouponFacade couponFacade;

	@Override
	@PostMapping("/publish")
	public ResponseEntity<CommonResponse<CouponResponse.Coupon>> publishCoupon(CouponRequest.Publish couponPublishRequest) {
		CouponResult.Coupon publish = couponFacade.publish(couponPublishRequest.toCommand());
		return ResponseEntity.ok(CommonResponse.success(CouponResponse.Coupon.of(publish)));
	}

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserCouponResponse.UserCoupons>> getUserCoupon(Long userId) {
		List<CouponResult.UserCoupon> userCoupons = couponFacade.getUserCoupons(userId);
		return ResponseEntity.ok(CommonResponse.success(UserCouponResponse.UserCoupons.of(userId, userCoupons)));
	}
}