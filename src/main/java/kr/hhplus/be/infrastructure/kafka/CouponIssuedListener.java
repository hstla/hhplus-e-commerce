package kr.hhplus.be.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.domain.shared.kafka.CouponIssuedEvent;
import kr.hhplus.be.domain.usercoupon.infrastructure.UserCouponSyncTask;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedListener {

	private final UserCouponRepository userCouponRepository;
	private final CouponRedisRepository couponRedisRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;

	@KafkaListener(topics = "coupon-issued", groupId = "coupon-db-sync-group")
	public void handleCouponIssuedEvent(CouponIssuedEvent event) {
		try {
			Long userId = event.userId();
			Long couponId = event.couponId();

			couponRedisRepository.markCouponIssued(couponId, userId);

			// DB에 최종 저장
			UserCoupon userCoupon = UserCoupon.publish(userId, couponId, event.issuedAt());
			userCouponRepository.save(userCoupon);

		} catch (Exception e) {
			log.error("Failed to sync user coupon to DB for event: {}. Error: {}", event, e.getMessage());
			// 실패 시, Dead Letter Queue(DLQ)로 메시지 발송
			userCouponRedisRepository.pushDeadLetterQueue(UserCouponSyncTask.of(event.userId(), event.couponId()));
		}
	}
}