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

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserPointResult;
import kr.hhplus.be.server.config.jpa.user.domain.model.Point;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserPointUseCase 단위 테스트")
class FindUserPointUseCaseTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private FindUserPointUseCase userPointUseCase;

	@Nested
	@DisplayName("유저 포인트 검색")
	class FindUserPointTest {

		@Test
		@DisplayName("포인트 검색에 성공한다")
		void execute() {
			// given
			Long userId = 1L;
			User findUser = new User(userId, "testName", "test@email.com", "1234", Point.of(1_000L));
			given(userRepository.findById(userId)).willReturn(findUser);

			// When
			UserPointResult.UserPoint userPoint = userPointUseCase.execute(userId);

			// Then
			assertThat(userPoint.getUserId()).isEqualTo(userId);
			assertThat(userPoint.getPoint()).isEqualTo(1_000L);

			verify(userRepository, times(1)).findById(userId);
		}
	}
}