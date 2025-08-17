package kr.hhplus.be.domain.coupon.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.coupon.model.Coupon;

public interface JpaCouponRepository extends JpaRepository<Coupon, Long> {

}