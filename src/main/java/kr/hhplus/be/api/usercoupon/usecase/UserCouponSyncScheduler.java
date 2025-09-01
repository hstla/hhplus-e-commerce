package kr.hhplus.be.api.usercoupon.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.infrastructure.UserCouponSyncTask;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponSyncScheduler {

	private final UserCouponRepository userCouponRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;
	private final CouponRedisRepository couponRedisRepository;

	// 1초 마다 스케줄러 실행
	@Scheduled(fixedDelay = 1000)
	public void syncDbWithRedis() {
		// DB 저장 큐에서 처리할 작업을 100개 꺼낸다.
		List<UserCouponSyncTask> tasks = userCouponRedisRepository.popDbSyncTask(100);

		if (tasks == null || tasks.isEmpty()) {
			return;
		}

		for (UserCouponSyncTask task  : tasks) {
			try {
				Long userId = task.userId();
				Long couponId = task.couponId();

				// Bitmap에 발급 기록을 남긴다. (중복 발급 방지 필터용)
				couponRedisRepository.markCouponIssued(couponId, userId);

				UserCoupon userCoupon = UserCoupon.publish(userId, couponId, LocalDateTime.now());
				userCouponRepository.save(userCoupon);

			} catch (Exception e) {
				// 5. 실패 시, 해당 작업을 Dead Letter Queue에 넣어 개발자가 수동으로 처리한다.
				userCouponRedisRepository.pushDeadLetterQueue(task);
			}
		}
	}
}