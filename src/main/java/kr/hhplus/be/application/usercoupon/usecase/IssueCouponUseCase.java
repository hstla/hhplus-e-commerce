package kr.hhplus.be.application.usercoupon.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.application.usercoupon.dto.UserCouponCommand;
import kr.hhplus.be.domain.common.event.CouponIssuedEventProducer;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IssueCouponUseCase {

	private final UserRepository userRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;
	private final CouponIssuedEventProducer couponEventProducer;

	public String execute(UserCouponCommand.Publish command) {
		LocalDateTime now = LocalDateTime.now();
		Long userId = command.userId();
		Long couponId = command.couponId();

		if (!userRepository.assertUserExists(userId)) {
			throw new RestApiException(UserErrorCode.INACTIVE_USER);
		}
		// 1. 사용자 연속 입력 방지 (Rate Limit) string 만들기.
		if (userCouponRedisRepository.isRateLimited(userId, couponId)) {
			throw new RestApiException(CouponErrorCode.TOO_MANY_REQUESTS);
		}
		// 2. 유효한 쿠폰인지 확인
		if (!userCouponRedisRepository.isCouponValid(couponId)) {
			throw new RestApiException(CouponErrorCode.INACTIVE_COUPON);
		}
		// 3. 사용자 중복 발급 방지
		if (userCouponRedisRepository.isDuplicateIssue(userId, couponId)) {
			throw new RestApiException(CouponErrorCode.DUPLICATED_COUPON);
		}

		String taskId = UUID.randomUUID().toString();

		couponEventProducer.sendCouponIssuedEvent(taskId, userId, couponId, now);
		return taskId;
	}
}