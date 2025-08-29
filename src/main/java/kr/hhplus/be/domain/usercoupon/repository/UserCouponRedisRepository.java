package kr.hhplus.be.domain.usercoupon.repository;

public interface UserCouponRedisRepository {
	boolean isRateLimited(Long userId, Long couponId);
	boolean isCouponValid(Long couponId);
	boolean isDuplicateIssue(Long userId, Long couponId);
	void addCouponIssueQueue(Long userId, Long couponId);
}