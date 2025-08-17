package kr.hhplus.be.domain.usercoupon.repository;

import java.util.List;

import kr.hhplus.be.domain.usercoupon.model.UserCoupon;

public interface UserCouponRepository {
	UserCoupon save(UserCoupon userCoupon);
	List<UserCoupon> findAllByUserId(Long userId);
	boolean existsByUserIdAndCouponId(Long userId, Long couponId);
	UserCoupon findById(Long userCouponId);
}