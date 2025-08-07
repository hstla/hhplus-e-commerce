package kr.hhplus.be.server.config.jpa.usercoupon.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;

public interface JpaUserCouponRepository extends JpaRepository<UserCoupon, Long> {
	List<UserCoupon> findAllByUserId(Long userId);
	boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}