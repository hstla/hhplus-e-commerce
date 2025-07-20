package kr.hhplus.be.server.config.jpa.coupon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.coupon.dto.CouponPublishRequest;
import kr.hhplus.be.server.config.jpa.coupon.dto.CouponResponse;
import kr.hhplus.be.server.config.jpa.coupon.dto.UserCouponResponse;

@RestController
@RequestMapping("/api/coupons")
public class CouponController implements CouponApiSpec{

	@Override
	@PostMapping("/publish")
	public ResponseEntity<CouponResponse> publishCoupon(CouponPublishRequest couponPublishRequest) {
		CouponResponse coupon = new CouponResponse(1L, "음식 쿠폰", CouponType.PERCENT, 10, false, LocalDateTime.now());
		return ResponseEntity.ok(coupon);
	}

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<UserCouponResponse> getUserCoupon(Long userId) {
		CouponResponse coupon1 = new CouponResponse(1L, "음식 쿠폰", CouponType.PERCENT, 10, true, LocalDateTime.now());
		CouponResponse coupon2 = new CouponResponse(2L, "옷 쿠폰", CouponType.FIXED, 1000, false, LocalDateTime.now());
		return ResponseEntity.ok(new UserCouponResponse(1L, List.of(coupon1, coupon2)));
	}
}