package kr.hhplus.be.server.config.jpa.coupon.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCouponRepository extends JpaRepository<CouponEntity, Long> {

}
