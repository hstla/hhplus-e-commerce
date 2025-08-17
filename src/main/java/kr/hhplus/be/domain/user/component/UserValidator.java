package kr.hhplus.be.domain.user.component;

import org.springframework.stereotype.Component;

import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import kr.hhplus.be.domain.user.repository.UserRepository;
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
}