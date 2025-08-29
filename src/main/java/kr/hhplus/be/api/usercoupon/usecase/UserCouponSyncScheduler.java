package kr.hhplus.be.api.usercoupon.usecase;

import static kr.hhplus.be.global.common.redis.RedisKeyName.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponSyncScheduler {

	private final RedisTemplate<String, String> redisTemplate;
	private final UserCouponRepository userCouponRepository;
	private final ObjectMapper objectMapper;

	// 1초 마다 스케줄러 실행
	@Scheduled(fixedDelay = 1000)
	public void syncDbWithRedis() {
		// 1. DB 저장 큐에서 처리할 작업을 10개 꺼낸다. (테스트를 위해 작게 설정)
		List<String> tasks = redisTemplate.opsForList().rightPop(COUPON_DB_SYNC_QUEUE.toKey(), 10);

		if (tasks == null || tasks.isEmpty()) {
			return;
		}

		for (String taskJson : tasks) {
			try {
				// 2. JSON 작업을 DTO로 변환
				UserCouponSyncTask task = objectMapper.readValue(taskJson, UserCouponSyncTask.class);
				Long userId = task.userId();
				Long couponId = task.couponId();

				// 3. Bitmap에 발급 기록을 남긴다. (중복 발급 방지 필터용)
				String issuedKey = COUPON_ISSUED_USER_BITMAP.toKey(couponId);
				redisTemplate.opsForValue().setBit(issuedKey, userId, true);

				// 4. 최종적으로 DB에 UserCoupon 정보를 저장한다.
				UserCoupon userCoupon = UserCoupon.publish(userId, couponId, LocalDateTime.now());
				userCouponRepository.save(userCoupon);

				log.info("DB 저장 성공 - userId: {}, couponId: {}", userId, couponId);

			} catch (Exception e) {
				log.error("DB 동기화 실패, Dead Letter Queue로 이동: {}", taskJson, e);
				// 5. 실패 시, 해당 작업을 Dead Letter Queue에 넣어 나중에 수동 처리할 수 있게 한다.
				redisTemplate.opsForList().leftPush(COUPON_DB_DEAD_LETTER_QUEUE.toKey(), taskJson);
			}
		}
	}
}