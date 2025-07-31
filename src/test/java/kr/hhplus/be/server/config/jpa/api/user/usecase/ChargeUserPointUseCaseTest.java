package kr.hhplus.be.server.config.jpa.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserPointResult;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.domain.model.Point;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChargeUserPointUseCase 단위 테스트")
class ChargeUserPointUseCaseTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private ChargeUserPointUseCase userPointUseCase;

	@Nested
	@DisplayName("유저 포인트 충전 검증")
	class ValidateEmailTest {

		@Test
		@DisplayName("포인트 충전에 성공한다")
		void execute() {
			// given
			Long userId = 1L;
			Long chargePoint = 1_000L;
			User findUser = new User(userId, "testName", "test@email.com", "1234", Point.of(1_000L));
			given(userRepository.findById(userId)).willReturn(findUser);
			given(userRepository.save(findUser)).willReturn(findUser);

			// When
			UserPointResult.UserPoint userPoint = userPointUseCase.execute(userId, chargePoint);

			// Then
			assertThat(userPoint.getUserId()).isEqualTo(userId);
			assertThat(userPoint.getPoint()).isEqualTo(2_000L); // 1_000 + 1_000

			verify(userRepository, times(1)).findById(userId);
			verify(userRepository, times(1)).save(any(User.class));
		}

		@Test
		@DisplayName("포인트 최소 충전을 넘지 못해서 포인트 충전에 실패한다")
		void execute_fail_invalidUserPoint() {
			// Given
			Long userId = 1L;
			Long chargePoint = 500L;
			User findUser = new User(userId, "testName", "test@email.com", "1234", Point.of(1_000L));
			given(userRepository.findById(userId)).willReturn(findUser);

			// When Then
			assertThatThrownBy(() -> userPointUseCase.execute(userId, chargePoint))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INVALID_USER_POINT.getMessage());

			verify(userRepository, times(1)).findById(userId);
			verify(userRepository, times(0)).save(any(User.class));
		}

		@ParameterizedTest
		@ValueSource(longs = {0L, -100L})
		@DisplayName("충전 포인트가 0이하는 실패한다")
		void execute_fail_invalidChargeAmount(Long chargePoint) {
			// Given
			Long userId = 1L;
			User findUser = new User(userId, "testName", "test@email.com", "1234", Point.of(1_000L));
			given(userRepository.findById(userId)).willReturn(findUser);

			// When Then
			assertThatThrownBy(() -> userPointUseCase.execute(userId, chargePoint))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INVALID_CHARGE_AMOUNT.getMessage());

			verify(userRepository, times(1)).findById(userId);
			verify(userRepository, times(0)).save(any(User.class));
		}
	}
}