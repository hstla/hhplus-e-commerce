package kr.hhplus.be.domain.common.event;

import java.time.LocalDateTime;

public interface CouponIssuedEventProducer {
    void sendCouponIssuedEvent(String taskId, Long userId, Long couponId, LocalDateTime now);
}