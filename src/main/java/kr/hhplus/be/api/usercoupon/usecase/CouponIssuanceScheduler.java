package kr.hhplus.be.api.usercoupon.usecase;

import static kr.hhplus.be.global.config.redis.RedisCacheName.*;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuanceScheduler {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	// 스케줄러1
	@Scheduled(fixedDelay = 100)
	public void processCouponIssuance() {
		Set<String> validCouponIds = redisTemplate.opsForSet().members(VALID_COUPONS);

		if (validCouponIds == null || validCouponIds.isEmpty()) {
			return;
		}

		for (String couponId : validCouponIds) {
			processQueueForCoupon(couponId);
		}
	}

	private void processQueueForCoupon(String couponId) {
		String queueKey = COUPON_QUEUE_PREFIX + couponId;
		String stockKey = COUPON_STOCK_PREFIX + couponId;

		// 3. 한 번에 100명씩 대기열에서 꺼낸다
		Set<ZSetOperations.TypedTuple<String>> userTuples = redisTemplate.opsForZSet().popMin(queueKey, 100);

		if (userTuples == null || userTuples.isEmpty()) {
			return;
		}

		for (ZSetOperations.TypedTuple<String> userTuple : userTuples) {
			String userId = userTuple.getValue();

			Long remainingStock = redisTemplate.opsForValue().increment(stockKey, -1L);

			if (remainingStock != null && remainingStock >= 0) {
				log.info("발급 성공 - userId: {}, couponId: {}, 남은 재고: {}", userId, couponId, remainingStock);
				try {
					String taskJson = objectMapper.writeValueAsString(DbSyncTask.of(Long.parseLong(userId), Long.parseLong(couponId)));
					redisTemplate.opsForList().leftPush(DB_WRITE_QUEUE, taskJson);
				} catch (Exception e) {
					log.error("DB 저장 작업 생성 실패: {}", e.getMessage());
					redisTemplate.opsForValue().increment(stockKey, 1L);
				}

				if (remainingStock == 0) {
					redisTemplate.opsForSet().remove(VALID_COUPONS, couponId);
					log.info("쿠폰 소진 완료: couponId {}", couponId);
				}

			} else {
				// 5-2. 발급 실패 (재고 없음): 루프를 중단하고 다음 쿠폰으로 넘어간다.
				log.warn("재고 없음 - userId: {}, couponId: {}", userId, couponId);
				// 방금 DECR로 음수가 된 재고를 다시 0으로 맞춰준다.
				redisTemplate.opsForValue().increment(stockKey, 1L);
				break;
			}
		}
	}
}