package kr.hhplus.be.infrastructure.kafka.consumer;

import static kr.hhplus.be.global.common.kafka.KafkaConstants.*;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.application.usercoupon.dto.UserCouponResult;
import kr.hhplus.be.domain.common.event.CouponIssuedSuccess;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import kr.hhplus.be.infrastructure.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponDBSyncConsumer {

	private final UserCouponRepository userCouponRepository;
	private final UserCouponRedisRepository userCouponRedisRepository;
	private final SseEmitterService sseEmitterService;

	@KafkaListener(topics = TOPIC_COUPON_ISSUED_SUCCESS, groupId = "user-coupon-db-sync-group")
	public void listen(CouponIssuedSuccess successDto) {
		try {
			log.info("usercoupon 생성 시작");
			UserCoupon userCoupon = UserCoupon.publish(
				successDto.userId(),
				successDto.couponId(),
				successDto.issuedAt()
			);
			userCouponRepository.save(userCoupon);

			handleSuccess(successDto.taskId(), successDto.issuedAt());
		} catch (DataIntegrityViolationException e) {
			log.warn("메시지가 이미 처리되었습니다 (멱등성). taskId: {}", successDto.taskId());

			handleSuccess(successDto.taskId(), successDto.issuedAt());
		} catch (Exception e) {
			log.error("쿠폰 발급 내역 저장 실패. DLQ 전송 필요. 데이터: {}", successDto, e);
			String failMessage = "서버에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
			UserCouponResult.UserCouponConfirmation failDto = UserCouponResult.UserCouponConfirmation.of(
				"FAIL",
				failMessage,
				successDto.issuedAt()
			);

			userCouponRedisRepository.saveIssueCouponTaskStatus(successDto.taskId(), "FAIL", failMessage, successDto.issuedAt());
			sseEmitterService.sendToUser(successDto.taskId(), "coupon-status", failDto);
		}
	}

	private void handleSuccess(String taskId, LocalDateTime issuedAt) {
		String successMessage = "쿠폰이 발급되었습니다.";
		UserCouponResult.UserCouponConfirmation success = UserCouponResult.UserCouponConfirmation.of(
			"SUCCESS",
			successMessage,
			issuedAt
		);

		userCouponRedisRepository.saveIssueCouponTaskStatus(taskId, "SUCCESS", successMessage, issuedAt);
		sseEmitterService.sendToUser(taskId, "coupon-status", success);
		log.info("쿠폰 발급 성공 처리 완료. taskId: {}", taskId);
	}
}