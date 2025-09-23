package kr.hhplus.be.infrastructure.persistence.coupon;

import org.springframework.stereotype.Component;

import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
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