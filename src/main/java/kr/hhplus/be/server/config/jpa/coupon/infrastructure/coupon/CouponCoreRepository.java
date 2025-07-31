package kr.hhplus.be.server.config.jpa.coupon.infrastructure.coupon;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.infrastructure.mapper.CouponMapper;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponCoreRepository implements CouponRepository {

	private final JpaCouponRepository jpaCouponRepository;
	private final CouponMapper couponMapper;

	@Override
	public Coupon save(Coupon coupon) {
		CouponEntity save = jpaCouponRepository.save(couponMapper.toEntity(coupon));
		return couponMapper.toModel(save);
	}

	@Override
	public Coupon findById(Long couponId) {
		CouponEntity findCouponEntity = jpaCouponRepository.findById(couponId).
			orElseThrow(() -> new RestApiException(CouponErrorCode.INACTIVE_COUPON));
		return couponMapper.toModel(findCouponEntity);
	}
}