package kr.hhplus.be.domain.usercoupon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import kr.hhplus.be.domain.common.event.UserCouponIssuanceStatusTask;
import kr.hhplus.be.domain.common.event.UserCouponSyncTask;

public interface UserCouponRedisRepository {
	boolean isRateLimited(Long userId, Long couponId);
	boolean isCouponValid(Long couponId);
	boolean isDuplicateIssue(Long userId, Long couponId);
	void pushDeadLetterQueue(UserCouponSyncTask task);
	List<UserCouponSyncTask> popDbSyncTask(int i);
	void addCouponIssueQueue(Long userId, Long couponId);
	void pushDbSyncTask(UserCouponSyncTask userCouponSyncTask);
	void saveIssueCouponTaskStatus(String taskId, String status, String message, LocalDateTime issuedAt);
	Optional<UserCouponIssuanceStatusTask> getIssueCouponTaskStatus(String taskId);
}