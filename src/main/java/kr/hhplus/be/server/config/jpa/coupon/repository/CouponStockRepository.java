package kr.hhplus.be.server.config.jpa.coupon.repository;

import kr.hhplus.be.server.config.jpa.coupon.model.CouponStock;

public interface CouponStockRepository {
	CouponStock save(CouponStock couponStock);
	CouponStock findWithLockByCouponId(Long couponId);
}