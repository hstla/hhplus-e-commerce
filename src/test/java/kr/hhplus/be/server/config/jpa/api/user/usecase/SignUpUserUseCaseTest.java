package kr.hhplus.be.server.config.jpa.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.any;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserCommand;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserResult;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.component.UserValidator;
import kr.hhplus.be.server.config.jpa.user.model.Point;
import kr.hhplus.be.server.config.jpa.user.model.User;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SignUpUserUseCase 단위 테스트")
class SignUpUserUseCaseTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private UserValidator userValidator;
	@InjectMocks
	private SignUpUserUseCase signUpUserUseCase;

	@Nested
	@DisplayName("유저 회원가입")
	class SignUpUserTest {

		@Test
		@DisplayName("유저 회원가입에 성공한다.")
		void execute() {
			// given
			Long userId = 1L;
			User findUser = new User(userId, "testName", "test@email.com", "12345", Point.zero());
			UserCommand.SignUp signUpRequest = UserCommand.SignUp.of("testName", "test@email.com", "12345");
			given(userRepository.save(any(User.class))).willReturn(findUser);

			// When
			UserResult.UserInfo userInfo = signUpUserUseCase.execute(signUpRequest);

			// Then
			assertThat(userInfo.id()).isEqualTo(userId);
			assertThat(userInfo.email()).isEqualTo("test@email.com");
			assertThat(userInfo.name()).isEqualTo("testName");

			verify(userValidator, times(1)).validateEmailNotDuplicated(findUser.getEmail());
		}

		@Test
		@DisplayName("중복된 이메일로 회원가입에 실패한다.")
		void execute_fail_duplicateEmail() {
			// given
			willThrow(new RestApiException(UserErrorCode.DUPLICATE_EMAIL)).given(userValidator).validateEmailNotDuplicated(any());
			UserCommand.SignUp signUpRequest = UserCommand.SignUp.of("testName", "test@email.com", "12345");

			// When then
			assertThatThrownBy(() -> signUpUserUseCase.execute(signUpRequest))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());

			verify(userRepository, times(0)).save(any(User.class));
		}
	}
}