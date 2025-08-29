package kr.hhplus.be.api.coupon.usecase;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.api.coupon.usecase.dto.CouponCommand;
import kr.hhplus.be.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponCreateUseCase {

	private final CouponRepository couponRepository;
	private final RedisTemplate<String, String> redisTemplate;

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
		String couponIdStr = String.valueOf(couponId);
		String stockStr = String.valueOf(stock);

		redisTemplate.opsForSet().add(COUPON_VALID_SET.toKey(), couponIdStr);
		redisTemplate.opsForValue().set(COUPON_STOCK_CACHE.toKey(couponId), stockStr);
	}
}