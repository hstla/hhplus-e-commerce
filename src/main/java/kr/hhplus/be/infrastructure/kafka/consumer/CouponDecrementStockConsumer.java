package kr.hhplus.be.infrastructure.kafka.consumer;

import static kr.hhplus.be.global.common.kafka.KafkaConstants.*;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.application.usercoupon.dto.UserCouponResult;
import kr.hhplus.be.domain.common.event.CouponIssuedEvent;
import kr.hhplus.be.domain.common.event.CouponIssuedSuccess;
import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import kr.hhplus.be.infrastructure.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponDecrementStockConsumer {

	private final CouponRedisRepository couponRedisRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final SseEmitterService sseEmitterService;

	@KafkaListener(topics = TOPIC_COUPON_ISSUED, groupId = "coupon-issuer-group")
	public void listen(CouponIssuedEvent event) {
		Long couponId = event.couponId();
		String taskId = event.taskId();

		Long decrementStock = couponRedisRepository.decrementStock(couponId);

		if (decrementStock != null && decrementStock >= 0) {
			log.info("재고 차감 성공. taskId: {}", taskId);

			CouponIssuedSuccess successDto = new CouponIssuedSuccess(
				taskId, event.userId(), event.couponId(), LocalDateTime.now()
			);
			kafkaTemplate.send(TOPIC_COUPON_ISSUED_SUCCESS, successDto);

			if (decrementStock == 0) {
				log.info("쿠폰 재고 소진 완료: couponId {}", couponId);
				couponRedisRepository.removeValidCoupon(couponId);
			}

		} else {
			log.warn("재고 없음. taskId: {}", taskId);

			if (decrementStock != null) {
				couponRedisRepository.incrementStock(couponId, 1L);
			}

			UserCouponResult.UserCouponConfirmation failDto = UserCouponResult.UserCouponConfirmation.of(
				"FAIL",
				"재고 없음",
				event.issuedAt()
			);
			userCouponRedisRepository.saveIssueCouponTaskStatus(taskId, "FAIL", "재고 없음", event.issuedAt());
			sseEmitterService.sendToUser(taskId, "coupon-status", failDto);
		}
	}
}