package kr.hhplus.be.domain.coupon.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.domain.coupon.model.CouponStock;

public interface JpaCouponStockRepository extends JpaRepository<CouponStock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT cs FROM CouponStock cs WHERE cs.couponId = :id")
	Optional<CouponStock> findWithLockByCouponId(@Param("id") Long couponId);
}