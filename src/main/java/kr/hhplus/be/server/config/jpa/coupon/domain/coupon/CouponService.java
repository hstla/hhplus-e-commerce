package kr.hhplus.be.server.config.jpa.coupon.domain.coupon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.coupon.application.CouponResult;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponRepository couponRepository;

	public CouponInfo.Info publishCoupon(Long couponId) {
		Coupon coupon = getCoupon(couponId);
		coupon.validatePublishability(LocalDateTime.now());

		coupon.decreaseQuantity();
		couponRepository.updateCoupon(coupon);
		return CouponInfo.Info.of(coupon);
	}

	public List<CouponInfo.Info> findCoupon(List<Long> couponIds) {
		List<Coupon> coupons = couponRepository.findAllById(couponIds);
		return coupons.stream().map(CouponInfo.Info::of).toList();
	}

	public CouponInfo.Info useCoupon(Long couponId) {
		Coupon coupon = getCoupon(couponId);
		coupon.validateNotExpired(LocalDateTime.now());
		return CouponInfo.Info.of(coupon);
	}

	private Coupon getCoupon(Long couponId) {
		return couponRepository.findById(couponId).orElseThrow(() -> new RestApiException(CouponErrorCode.INACTIVE_COUPON));
	}
}