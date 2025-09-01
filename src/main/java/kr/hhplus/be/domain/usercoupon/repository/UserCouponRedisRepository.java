package kr.hhplus.be.domain.usercoupon.repository;

import java.util.List;

import kr.hhplus.be.domain.usercoupon.infrastructure.UserCouponSyncTask;

public interface UserCouponRedisRepository {
	boolean isRateLimited(Long userId, Long couponId);
	boolean isCouponValid(Long couponId);
	boolean isDuplicateIssue(Long userId, Long couponId);
	void addCouponIssueQueue(Long userId, Long couponId);
	void pushDbSyncTask(UserCouponSyncTask userCouponSyncTask);
	List<UserCouponSyncTask> popDbSyncTask(int i);
	void pushDeadLetterQueue(UserCouponSyncTask task);
}