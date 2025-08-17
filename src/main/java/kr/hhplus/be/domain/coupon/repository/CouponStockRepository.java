package kr.hhplus.be.domain.coupon.repository;

import kr.hhplus.be.domain.coupon.model.CouponStock;

public interface CouponStockRepository {
	CouponStock save(CouponStock couponStock);
	CouponStock findWithLockByCouponId(Long couponId);
}