package kr.hhplus.be.api.usercoupon.usecase;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;

import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.infrastructure.UserCouponSyncTask;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuanceScheduler {

	private final CouponRedisRepository couponRedisRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;


	// 스케줄러1
	@Scheduled(fixedDelay = 100)
	public void processCouponIssuance() {
		Set<Long> validCouponIds = couponRedisRepository.getValidCouponIds();

		if (validCouponIds == null || validCouponIds.isEmpty()) {
			return;
		}

		for (Long couponId : validCouponIds) {
			processQueueForCoupon(couponId);
		}
	}

	private void processQueueForCoupon(Long couponId) {
		Long queueKey = Long.getLong(COUPON_ISSUE_QUEUE.toKey(couponId));
		Long stockKey = Long.getLong(COUPON_STOCK_CACHE.toKey(couponId));

		// 3. 한 번에 100명씩 대기열에서 꺼낸다
		Set<ZSetOperations.TypedTuple<Long>> userTuples = couponRedisRepository.popCouponIssueQueue(queueKey, 100);

		if (userTuples == null || userTuples.isEmpty()) {
			return;
		}

		for (ZSetOperations.TypedTuple<Long> userTuple : userTuples) {
			Long userId = userTuple.getValue();
			Long remainingStock = couponRedisRepository.decrementStock(stockKey);

			if (remainingStock != null && remainingStock >= 0) {
				try {
					userCouponRedisRepository.pushDbSyncTask(UserCouponSyncTask.of(userId, couponId));
				} catch (Exception e) {
					log.error("DB 저장 작업 생성 실패: {}", e.getMessage());
					couponRedisRepository.incrementStock(stockKey, 1L);
				}

				if (remainingStock == 0) {
					couponRedisRepository.removeValidCoupon(couponId);
					couponRedisRepository.removeCouponStock(couponId);

					log.info("쿠폰 소진 완료: couponId {}", couponId);
				}

			} else {
				// 5-2. 발급 실패 (재고 없음): 루프를 중단하고 다음 쿠폰으로 넘어간다.
				log.warn("재고 없음 - userId: {}, couponId: {}", userId, couponId);
				// 방금 DECR로 음수가 된 재고를 다시 0으로 맞춰준다.
				couponRedisRepository.incrementStock(stockKey, 1L);
				break;
			}
		}
	}
}