package kr.hhplus.be.server.config.jpa.coupon.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponStock;

public interface JpaCouponStockRepository extends JpaRepository<CouponStock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT cs FROM CouponStock cs WHERE cs.id = :id")
	Optional<CouponStock> findByIdWithLock(@Param("id") Long stockId);
}