package kr.hhplus.be.server.config.jpa.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserResult;
import kr.hhplus.be.server.config.jpa.user.domain.model.Point;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserUseCase 단위 테스트")
class FindUserUseCaseTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private FindUserUseCase userUseCase;

	@Nested
	@DisplayName("유저 검색")
	class FindUserTest {

		@Test
		@DisplayName("유저 검색에 성공한다")
		void execute() {
			// given
			Long userId = 1L;
			User findUser = new User(userId, "testName", "test@email.com", "1234", Point.zero());
			given(userRepository.findById(userId)).willReturn(findUser);

			// When
			UserResult.UserInfo userInfo = userUseCase.execute(userId);

			// Then
			assertThat(userInfo.getId()).isEqualTo(userId);
			assertThat(userInfo.getEmail()).isEqualTo("test@email.com");
			assertThat(userInfo.getName()).isEqualTo("testName");

			verify(userRepository, times(1)).findById(userId);
		}
	}
}