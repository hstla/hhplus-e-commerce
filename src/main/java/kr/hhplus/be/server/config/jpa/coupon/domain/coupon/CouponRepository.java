package kr.hhplus.be.server.config.jpa.coupon.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

	Coupon save(Coupon coupon);
	Optional<Coupon> findById(Long couponId);
	void clear();
	void updateCoupon(Coupon coupon);
	List<Coupon> findAllById(List<Long> couponIds);
}
