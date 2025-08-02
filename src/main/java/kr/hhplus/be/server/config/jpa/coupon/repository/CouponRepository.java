package kr.hhplus.be.server.config.jpa.coupon.repository;

import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;

public interface CouponRepository {
	Coupon save(Coupon coupon);
	Coupon findById(Long couponId);
}