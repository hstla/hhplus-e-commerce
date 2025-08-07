package kr.hhplus.be.server.config.jpa.coupon.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;

public interface JpaCouponRepository extends JpaRepository<Coupon, Long> {

}
