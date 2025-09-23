package kr.hhplus.be.infrastructure.persistence.usercoupon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.usercoupon.model.UserCoupon;

public interface JpaUserCouponRepository extends JpaRepository<UserCoupon, Long> {
	List<UserCoupon> findAllByUserId(Long userId);
	boolean existsByUserIdAndCouponId(Long userId, Long couponId);
	List<UserCoupon> findAllByCouponId(Long id);
}