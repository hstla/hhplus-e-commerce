package kr.hhplus.be.infrastructure.redis.usercoupon;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.common.event.UserCouponIssuanceStatusTask;
import kr.hhplus.be.domain.common.event.UserCouponSyncTask;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCouponRedisRepositoryImpl implements UserCouponRedisRepository {

   	private final RedisTemplate<String, Long> longRedisTemplate;
   	private final RedisTemplate<String, String> stringRedisTemplate;
   	private final RedisTemplate<String, UserCouponSyncTask> userCouponRedisTemplate;
	private final RedisTemplate<String, UserCouponIssuanceStatusTask> userCouponIssuanceStatusRedisTemplate;

	@Override
	public boolean isRateLimited(Long userId, Long couponId) {
		String key = COUPON_ISSUE_REQUEST_LIMIT.toKey(userId, couponId);
		Boolean isNewKey = longRedisTemplate.opsForValue().setIfAbsent(key, 1L, COUPON_ISSUE_REQUEST_LIMIT.getTtl());
		return Boolean.FALSE.equals(isNewKey);
	}

	@Override
	public boolean isCouponValid(Long couponId) {
		Boolean isMember = longRedisTemplate.opsForSet().isMember(COUPON_VALID_SET.toKey(), couponId);
		return Boolean.TRUE.equals(isMember);
	}

	@Override
	public boolean isDuplicateIssue(Long userId, Long couponId) {
		String key = COUPON_ISSUED_USER_BITMAP.toKey(couponId);
		Boolean hasIssued = longRedisTemplate.opsForValue().getBit(key, userId);
		return Boolean.TRUE.equals(hasIssued);
	}

	@Override
	public void addCouponIssueQueue(Long userId, Long couponId) {
		String queueKey = COUPON_ISSUE_QUEUE.toKey(couponId);
		long timestamp = System.currentTimeMillis();
		longRedisTemplate.opsForZSet().add(queueKey, userId, timestamp);
	}

	@Override
	public void pushDbSyncTask(UserCouponSyncTask userCouponSyncTask) {
		userCouponRedisTemplate.opsForList().leftPush(COUPON_DB_SYNC_QUEUE.toKey(), userCouponSyncTask);
	}

	@Override
	public List<UserCouponSyncTask> popDbSyncTask(int i) {
		return userCouponRedisTemplate.opsForList().rightPop(COUPON_DB_SYNC_QUEUE.toKey(), i);
	}

	@Override
	public void pushDeadLetterQueue(UserCouponSyncTask task) {
		userCouponRedisTemplate.opsForList().leftPush(COUPON_DB_DEAD_LETTER_QUEUE.toKey(), task);
	}

	@Override
	public void saveIssueCouponTaskStatus(String taskId, String status, String message, LocalDateTime issuedAt) {
		String resultKey = COUPON_ISSUE_TASK_STATUS.toKey(taskId);
		UserCouponIssuanceStatusTask task = UserCouponIssuanceStatusTask.of(status, message, issuedAt);

		userCouponIssuanceStatusRedisTemplate.opsForValue().set(resultKey, task, COUPON_ISSUE_TASK_STATUS.getTtl());
	}

	@Override
	public Optional<UserCouponIssuanceStatusTask> getIssueCouponTaskStatus(String taskId) {
		String key = COUPON_ISSUE_TASK_STATUS.toKey(taskId);

		UserCouponIssuanceStatusTask task = userCouponIssuanceStatusRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(task);
	}
}