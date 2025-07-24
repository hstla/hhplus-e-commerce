package kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
	UserCoupon save(UserCoupon userCoupon);
	void clear();
	boolean existsByUserAndCoupon(long userId, long couponId);
	List<UserCoupon> findAllByUserId(Long userId);
	Optional<UserCoupon> findByIdAndUserId(Long userId, Long userCouponId);
}