package kr.hhplus.be.application.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPointLockManager {

	private final UserRepository userRepository;

	@Transactional
	public boolean deductPointWithLock(long userId, long totalAmount) {
		User findUser = userRepository.findByIdWithLock(userId);
		findUser.usePoint(totalAmount);
		userRepository.save(findUser);
		return true;
	}
}