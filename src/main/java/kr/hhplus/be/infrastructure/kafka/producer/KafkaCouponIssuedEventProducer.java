package kr.hhplus.be.infrastructure.kafka.producer;

import static kr.hhplus.be.global.common.kafka.KafkaConstants.*;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import kr.hhplus.be.domain.common.event.CouponIssuedEvent;
import kr.hhplus.be.domain.common.event.CouponIssuedEventProducer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaCouponIssuedEventProducer implements CouponIssuedEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void sendCouponIssuedEvent(String taskId, Long userId, Long couponId,  LocalDateTime now) {
		CouponIssuedEvent event = new CouponIssuedEvent(taskId, userId, couponId, now);
		kafkaTemplate.send(TOPIC_COUPON_ISSUED, String.valueOf(couponId), event);
	}
}