package kr.hhplus.be.application.coupon.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.application.coupon.dto.CouponCommand;
import kr.hhplus.be.application.coupon.dto.CouponResult;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponCreateUseCase {

	private final CouponRepository couponRepository;
	private final CouponRedisRepository couponRedisRepository;

	@Transactional
	public CouponResult.CouponDetail execute(CouponCommand.CouponCreate command) {
		Coupon coupon = Coupon.create(
			command.name(),
			command.couponType(),
			command.discountValue(),
			command.initialStock(),
			command.expireAt()
		);
		Coupon savedCoupon = couponRepository.save(coupon);

		addCouponToRedis(savedCoupon.getId(), command.initialStock());
		return CouponResult.CouponDetail.of(savedCoupon);
	}

	private void addCouponToRedis(Long couponId, int stock) {
		couponRedisRepository.addCouponValidSet(couponId);
		couponRedisRepository.addCouponStock(couponId, stock);
	}
}