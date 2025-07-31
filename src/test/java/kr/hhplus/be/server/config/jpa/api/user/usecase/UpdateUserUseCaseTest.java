package kr.hhplus.be.server.config.jpa.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;

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
import kr.hhplus.be.server.config.jpa.user.domain.model.Point;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserUseCase 단위 테스트")
class UpdateUserUseCaseTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UpdateUserUseCase updateUserUseCase;

	@Nested
	@DisplayName("유저 이름 수정")
	class SignUpUserTest {

		@Test
		@DisplayName("유저 이름 수정에 성공한다")
		void execute() {
			// given
			Long userId = 1L;
			User findUser = new User(userId, "testName", "test@email.com", "12345", Point.zero());
			User updatedUSer = new User(userId, "updateName", "test@email.com", "12345", Point.zero());
			given(userRepository.findById(userId)).willReturn(findUser);
			UserCommand.Update updateName = UserCommand.Update.of("updateName");
			given(userRepository.save(any(User.class))).willReturn(updatedUSer);

			// When
			UserResult.UserInfo userInfo = updateUserUseCase.execute(userId, updateName);

			// Then
			assertThat(userInfo.getId()).isEqualTo(userId);
			assertThat(userInfo.getEmail()).isEqualTo("test@email.com");
			assertThat(userInfo.getName()).isEqualTo("updateName");

			verify(userRepository, times(1)).findById(userId);
			verify(userRepository, times(1)).save(any(User.class));
		}

		@Test
		@DisplayName("유저 아이디 검증에 실패하여 수정할 수 없다")
		void execute_fail() {
			// given
			Long userId = 1L;
			UserCommand.Update updateRequest = UserCommand.Update.of("newName");

			when(userRepository.findById(userId)).thenThrow(new RestApiException(UserErrorCode.INACTIVE_USER));

			// when & then
			assertThatThrownBy(() -> updateUserUseCase.execute(userId, updateRequest))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INACTIVE_USER.getMessage());

			verify(userRepository, times(1)).findById(userId);
		}
	}
}