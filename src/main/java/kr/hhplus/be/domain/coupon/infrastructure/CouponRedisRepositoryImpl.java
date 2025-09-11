package kr.hhplus.be.domain.coupon.infrastructure;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepositoryImpl implements CouponRedisRepository {

	private final RedisTemplate<String, Integer> intRedisTemplate;
	private final RedisTemplate<String, Long> longRedisTemplate;


	@Override
	public Long addCouponValidSet(Long couponId) {
		return longRedisTemplate.opsForSet().add(COUPON_VALID_SET.toKey(), couponId);
	}

	@Override
	public void addCouponStock(Long couponId, int stock) {
		intRedisTemplate.opsForValue().set(COUPON_STOCK_CACHE.toKey(couponId), stock);
	}

	@Override
	public Set<Long> getValidCouponIds() {
		return longRedisTemplate.opsForSet().members(COUPON_VALID_SET.toKey());
	}

	@Override
	public Set<ZSetOperations.TypedTuple<Long>> popCouponIssueQueue(Long couponId, int count) {
		return longRedisTemplate.opsForZSet().popMin(COUPON_ISSUE_QUEUE.toKey(couponId), count);
	}

	@Override
	public Long decrementStock(Long couponId) {
		return longRedisTemplate.opsForValue().increment(COUPON_STOCK_CACHE.toKey(couponId), -1L);
	}

	@Override
	public void incrementStock(Long couponId, long value) {
		longRedisTemplate.opsForValue().increment(COUPON_STOCK_CACHE.toKey(couponId), value);
	}

	@Override
	public void removeValidCoupon(Long couponId) {
		longRedisTemplate.opsForSet().remove(COUPON_VALID_SET.toKey(), couponId);
	}

	@Override
	public void removeCouponStock(Long couponId) {
		intRedisTemplate.delete(COUPON_STOCK_CACHE.toKey(couponId));
	}

	@Override
	public void markCouponIssued(Long couponId, Long userId) {
		String issuedKey = COUPON_ISSUED_USER_BITMAP.toKey(couponId);
		longRedisTemplate.opsForValue().setBit(issuedKey, userId, true);
	}
}