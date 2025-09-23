package kr.hhplus.be.application.user.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.application.user.dto.UserCommand;
import kr.hhplus.be.application.user.dto.UserResult;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignUpUserUseCase {

	private final UserRepository userRepository;

	@Transactional
	public UserResult.UserInfo execute(UserCommand.SignUp signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.email())) {
			throw new RestApiException(UserErrorCode.DUPLICATE_EMAIL);
		}

		User newUser = User.create(signUpRequest.name(), signUpRequest.email(), signUpRequest.password());
		User savedUser = userRepository.save(newUser);

		return UserResult.UserInfo.of(savedUser);
	}
}
