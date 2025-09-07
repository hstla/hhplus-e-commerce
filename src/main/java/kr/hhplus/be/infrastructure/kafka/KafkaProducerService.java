package kr.hhplus.be.infrastructure.kafka;

import static kr.hhplus.be.global.common.kafka.KafkaConstants.*;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import kr.hhplus.be.domain.shared.kafka.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

	private final KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate;

	public void sendCouponIssuedEvent(Long userId, Long couponId) {
		CouponIssuedEvent event = new CouponIssuedEvent(userId, couponId, LocalDateTime.now());
		kafkaTemplate.send(TOPIC_COUPON_ISSUED, String.valueOf(couponId), event);
	}
}