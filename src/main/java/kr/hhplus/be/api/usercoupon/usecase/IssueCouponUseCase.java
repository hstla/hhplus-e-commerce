package kr.hhplus.be.api.usercoupon.usecase;

import org.springframework.stereotype.Component;

import kr.hhplus.be.api.usercoupon.usecase.dto.UserCouponCommand;
import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import kr.hhplus.be.infrastructure.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IssueCouponUseCase {

	private final UserRepository userRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;
	private final CouponRedisRepository couponRedisRepository;
	private final KafkaProducerService kafkaProducerService;

	public void execute(UserCouponCommand.Publish command) {
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

		// 4. 즉시 쿠폰 재고 차감.
		Long remainingStock = couponRedisRepository.decrementStock(couponId);

		if (remainingStock != null && remainingStock >= 0) {
			// 5. 성공 시, Kafka에 이벤트 발행 (후속 처리를 위임)
			try {
				kafkaProducerService.sendCouponIssuedEvent(userId, couponId);
			} catch (Exception e) {
				// Kafka 발행 실패 시, 차감했던 재고를 다시 복구 (보상 트랜잭션)
				couponRedisRepository.incrementStock(couponId, 1L);
				throw new RestApiException(CouponErrorCode.KAFKA_PRODUCE_FAILED);
			}
		} else {
			// 6. 재고 소진 시, 롤백 및 실패 응답
			// decrementStock으로 음수가 된 재고를 다시 0으로 맞춰줍니다.
			if (remainingStock != null) {
				couponRedisRepository.incrementStock(couponId, 1L);
			}
			throw new RestApiException(CouponErrorCode.OUT_OF_STOCK_COUPON);
		}
	}
}