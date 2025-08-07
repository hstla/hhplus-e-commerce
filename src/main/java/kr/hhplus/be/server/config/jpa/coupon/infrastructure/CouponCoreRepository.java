package kr.hhplus.be.server.config.jpa.coupon.infrastructure;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponCoreRepository implements CouponRepository {

	private final JpaCouponRepository jpaCouponRepository;

	@Override
	public Coupon save(Coupon coupon) {
		return jpaCouponRepository.save(coupon);
	}

	@Override
	public Coupon findById(Long couponId) {
		return jpaCouponRepository.findById(couponId).
			orElseThrow(() -> new RestApiException(CouponErrorCode.INACTIVE_COUPON));
	}
}