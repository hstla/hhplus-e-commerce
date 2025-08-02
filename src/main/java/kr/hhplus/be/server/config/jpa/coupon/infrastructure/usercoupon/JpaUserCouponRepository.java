package kr.hhplus.be.server.config.jpa.coupon.infrastructure.usercoupon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserCouponRepository extends JpaRepository<UserCouponEntity, Long> {

	List<UserCouponEntity> findAllByUserId(Long userId);
	Optional<UserCouponEntity> findByUserIdAndCouponId(Long userId, Long couponId);
}