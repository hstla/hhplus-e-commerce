package kr.hhplus.be.server.config.jpa.user.domain.service;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public void pointPay(long userId, long totalAmount) {
		User findUser = userRepository.findById(userId);
		findUser.usePoint(totalAmount);
		userRepository.save(findUser);
	}
}