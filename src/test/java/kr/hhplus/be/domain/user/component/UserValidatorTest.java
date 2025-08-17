package kr.hhplus.be.domain.user.component;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;

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
}