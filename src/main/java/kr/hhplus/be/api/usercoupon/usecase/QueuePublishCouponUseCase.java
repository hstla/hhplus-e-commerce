package kr.hhplus.be.api.usercoupon.usecase;

import static kr.hhplus.be.global.config.redis.RedisCacheName.*;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.api.usercoupon.usecase.dto.UserCouponCommand;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueuePublishCouponUseCase {

	private final RedisTemplate<String, String> redisTemplate;
	private final UserRepository userRepository;

	public void execute(UserCouponCommand.Publish command) {
		Long userId = command.userId();
		Long couponId = command.couponId();
		String userIdStr = String.valueOf(userId);
		String couponIdStr = String.valueOf(couponId);

		userRepository.assertUserExists(userId);

		// 1. 사용자 연속 입력 방지 (Rate Limit)
		checkRateLimit(userId, couponId);
		// 2. 유효한 쿠폰인지 확인
		checkIfCouponIsValid(couponIdStr);
		// 3. 사용자 중복 발급 방지
		checkDuplicateIssuance(userId, couponIdStr);
		// 4. 모든 검증 통과 후, 대기열에 추가 - todo 재고 감소 로직 추가 후 db 연동 큐로 바뀌어야 함.
		addToQueue(userIdStr, couponIdStr);
	}

	// String 자료구조 3분 이내 재시도는 에러처리 (string)
	private void checkRateLimit(Long userId, Long couponId) {
		String key = RATE_LIMIT_PREFIX + userId + ":" + couponId;
		Long count = redisTemplate.opsForValue().increment(key);

		if (count != null && count > 1) {
			throw new RestApiException(CouponErrorCode.TOO_MANY_REQUESTS);
		}

		redisTemplate.expire(key, Duration.ofMinutes(3));
	}

	// 유효한 쿠폰인지 확인 (set) Long
	private void checkIfCouponIsValid(String couponIdStr) {
		Boolean isMember = redisTemplate.opsForSet().isMember(VALID_COUPONS, couponIdStr);
		if (Boolean.FALSE.equals(isMember)) {
			throw new RestApiException(CouponErrorCode.INACTIVE_COUPON);
		}
	}

	// 사용자가 중복된 쿠폰이 있는지 확인 (bitMap)
	private void checkDuplicateIssuance(Long userId, String couponIdStr) {
		String key = COUPON_ISSUED_PREFIX + couponIdStr;
		Boolean hasIssued = redisTemplate.opsForValue().getBit(key, userId);
		if (Boolean.TRUE.equals(hasIssued)) {
			throw new RestApiException(CouponErrorCode.DUPLICATED_COUPON);
		}
	}

	private void addToQueue(String userIdStr, String couponIdStr) {
		String queueKey = COUPON_QUEUE_PREFIX + couponIdStr;
		long timestamp = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(queueKey, userIdStr, timestamp);
	}
}