package kr.hhplus.be.application.usercoupon.usecase;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.application.usercoupon.dto.UserCouponResult;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRedisRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetCouponIssuanceStatusUseCase {

	private final UserCouponRedisRepository userCouponRedisRepository;

	public Optional<UserCouponResult.UserCouponConfirmation> execute(String taskId) {
		return userCouponRedisRepository.getIssueCouponTaskStatus(taskId)
			.map( task -> UserCouponResult.UserCouponConfirmation.of(
				task.status(),
				task.message(),
				task.issuedAt()
		));
	}
}