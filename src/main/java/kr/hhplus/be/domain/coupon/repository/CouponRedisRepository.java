package kr.hhplus.be.domain.coupon.repository;

import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;

public interface CouponRedisRepository {
	Long addCouponValidSet(Long couponId);
	void addCouponStock(Long couponId, int stock);
	Set<Long> getValidCouponIds();
	Set<ZSetOperations.TypedTuple<Long>> popCouponIssueQueue(String couponId, int count);
	Long decrementStock(String stockKey);
	void incrementStock(String couponId, long value);
	void removeValidCoupon(Long couponId);
	void removeCouponStock(Long couponId);
	void markCouponIssued(Long couponId, Long userId);
}
