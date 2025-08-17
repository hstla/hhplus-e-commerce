package kr.hhplus.be.domain.coupon.repository;

import kr.hhplus.be.domain.coupon.model.Coupon;

public interface CouponRepository {
	Coupon save(Coupon coupon);
	Coupon findById(Long couponId);
}