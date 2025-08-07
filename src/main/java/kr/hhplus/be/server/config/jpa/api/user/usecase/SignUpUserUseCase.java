package kr.hhplus.be.server.config.jpa.api.user.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserCommand;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserResult;
import kr.hhplus.be.server.config.jpa.user.component.UserValidator;
import kr.hhplus.be.server.config.jpa.user.model.User;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignUpUserUseCase {

	private final UserRepository userRepository;
	private final UserValidator userValidator;

	@Transactional
	public UserResult.UserInfo execute(UserCommand.SignUp signUpRequest) {
		userValidator.validateEmailNotDuplicated(signUpRequest.email());

		User newUser = User.create(signUpRequest.name(), signUpRequest.email(), signUpRequest.password());
		User savedUser = userRepository.save(newUser);

		return UserResult.UserInfo.of(savedUser);
	}
}
