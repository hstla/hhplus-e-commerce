package kr.hhplus.be.application.user.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.application.user.dto.UserPointResult;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChargeUserPointUseCase {

	private final UserRepository userRepository;

	@Transactional
	public UserPointResult.UserPoint execute(Long userId, Long chargePointAmount) {
		User findUser = userRepository.findById(userId);
		findUser.chargePoint(chargePointAmount);

		User savedUser = userRepository.save(findUser);
		return UserPointResult.UserPoint.of(savedUser);
	}
}