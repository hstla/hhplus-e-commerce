package kr.hhplus.be.server.config.jpa.user.component;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {

	private final UserRepository userRepository;

	public void validateEmailNotDuplicated(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new RestApiException(UserErrorCode.DUPLICATE_EMAIL);
		}
	}

	public void validateExistingUser(Long userId) {
		userRepository.findById(userId);
	}
}