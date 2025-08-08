package kr.hhplus.be.server.config.jpa.user.component;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.model.Point;
import kr.hhplus.be.server.config.jpa.user.model.User;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserValidator 단위 테스트")
class UserValidatorTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserValidator userValidator;

	@Nested
	@DisplayName("이메일 중복 검증")
	class ValidateEmailTest {

		@Test
		@DisplayName("중복되지 않은 이메일인 경우 성공한다")
		void existsByEmail_success() {
			// given
			given(userRepository.existsByEmail(anyString())).willReturn(false);

			// When Then
			assertThatNoException().isThrownBy(() ->
				userValidator.validateEmailNotDuplicated("test@example.com")
			);
		}

		@Test
		@DisplayName("중복된 이메일인 경우 예외 발생한다")
		void existsByEmail_duplicate_email() {
			// Given
			given(userRepository.existsByEmail(anyString())).willReturn(true);

			// When Then
			assertThatThrownBy(() -> userValidator.validateEmailNotDuplicated("duplicate@example.com"))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
		}
	}

	@Nested
	@DisplayName("사용자 존재 여부 검증")
	class ValidateUserTest {

		@Test
		@DisplayName("존재하는 사용자인 경우 성공한다")
		void findById_success() {
			// given
			Long userId = 1L;
			User mockUser = new User(userId, "testName", "test@mail.com", "1234", Point.of(100L));
			given(userRepository.findById(userId)).willReturn(mockUser);

			// when then
			assertThatNoException().isThrownBy(() ->
				userValidator.validateExistingUser(userId)
			);

			verify(userRepository, times(1)).findById(userId);
		}

		@Test
		@DisplayName("존재하지 않는 사용자인 경우 예외 발생한다")
		void findById_fail() {
			// given
			Long nonExistingUserId = 999L;
			given(userRepository.findById(nonExistingUserId)).willThrow(new RestApiException(UserErrorCode.INACTIVE_USER));

			// when then
			assertThatThrownBy(() -> userValidator.validateExistingUser(nonExistingUserId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INACTIVE_USER.getMessage());

			verify(userRepository, times(1)).findById(any());
		}
	}
}