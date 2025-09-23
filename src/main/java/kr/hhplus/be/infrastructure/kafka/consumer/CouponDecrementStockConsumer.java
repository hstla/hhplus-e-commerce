package kr.hhplus.be.infrastructure.kafka.consumer;

import static kr.hhplus.be.global.common.kafka.KafkaConstants.*;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.domain.common.event.CouponIssuedEvent;
import kr.hhplus.be.domain.common.event.CouponIssuedSuccess;
import kr.hhplus.be.domain.common.event.UserCouponSyncTask;
import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedConsumer {

	private final UserCouponRepository userCouponRepository;
	private final CouponRedisRepository couponRedisRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@KafkaListener(topics = TOPIC_COUPON_ISSUED, groupId = "coupon-issuer-group")
	public void listen(CouponIssuedEvent event) {
		Long couponId = event.couponId();

		Long decrementStock = couponRedisRepository.decrementStock(couponId);

		if (decrementStock != null && decrementStock >= 0) {
			log.info("재고 차감 성공. taskId: {}", event.taskId());

			CouponIssuedSuccess successDto = new CouponIssuedSuccess(
				event.taskId(), event.userId(), event.couponId(), LocalDateTime.now()
			);
			kafkaTemplate.send(TOPIC_COUPON_ISSUED_SUCCESS, successDto);
		} else {
			if (decrementStock != null) {
				couponRedisRepository.incrementStock(event.couponId(), 1L);
			}
			couponRedisRepository.removeValidCoupon(couponId);
			log.warn("재고 없음. taskId: {}", event.taskId());
		}
	}
}