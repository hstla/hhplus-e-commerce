package kr.hhplus.be.server.config.jpa.coupon.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponCoreRepository implements CouponRepository {

	private final JpaCouponRepository jpaCouponRepository;

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
