package kr.hhplus.be.server.config.jpa.coupon.repository;

import java.util.List;

import kr.hhplus.be.server.config.jpa.coupon.model.UserCoupon;

public interface UserCouponRepository {
	UserCoupon save(UserCoupon userCoupon);
	List<UserCoupon> findAllByUserId(Long userId);
	boolean existsByUserIdAndCouponId(Long userId, Long couponId);
	UserCoupon findById(Long userCouponId);
}