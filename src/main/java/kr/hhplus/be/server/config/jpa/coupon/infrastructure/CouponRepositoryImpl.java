package kr.hhplus.be.server.config.jpa.coupon.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponRepository;

@Component
public class CouponRepositoryImpl implements CouponRepository {
	@Override
	public Coupon save(Coupon coupon) {
		return null;
	}

	@Override
	public Optional<Coupon> findById(Long couponId) {
		return Optional.empty();
	}

	@Override
	public void clear() {

	}

	@Override
	public void updateCoupon(Coupon coupon) {

	}

	@Override
	public List<Coupon> findAllById(List<Long> couponIds) {
		return List.of();
	}
}
