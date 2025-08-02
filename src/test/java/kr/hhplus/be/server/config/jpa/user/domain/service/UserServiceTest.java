package kr.hhplus.be.server.config.jpa.user.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.domain.model.Point;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceTest 단위 테스트")
class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserService userService;


	@Nested
	@DisplayName("point pay 메서드 테스트")
	class PointPay {

		@Test
		@DisplayName("포인트 결제 성공한다")
		void pointPaySuccess() {
			// given
			Long userId = 1L;
			User mockUser = new User(userId, "testName", "test@mail.com", "1234", Point.of(2_000L));
			long totalAmount = 500L;
			given(userRepository.findById(userId)).willReturn(mockUser);

			// when
			userService.pointPay(userId, totalAmount);

			// then
			assertThat(mockUser.getPoint().getAmount()).isEqualTo(1_500L); // 2000 - 500 = 500
			verify(userRepository, times(1)).findById(1L);
			verify(userRepository, times(1)).save(any(User.class));
		}

		@Test
		@DisplayName("포인트 부족으로 인한 결제 실패한다")
		void pointPayFail_point_lack() {
			// given
			Long userId = 1L;
			User mockUser = new User(userId, "testName", "test@mail.com", "1234", Point.of(100L));
			long totalAmount = 500L;

			given(userRepository.findById(userId)).willReturn(mockUser);

			// when then
			Assertions.assertThatThrownBy(() -> userService.pointPay(userId, totalAmount))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INSUFFICIENT_USER_POINT.getMessage());

			// findById는 호출되었으나, 포인트 부족으로 save는 호출되지 않았는지 검증
			verify(userRepository, times(1)).findById(userId);
			verify(userRepository, never()).save(any(User.class));
		}

		@Test
		@DisplayName("포인트가 0이하인 경우 예외처리를 한다")
		void pointPayFail_not_userId() {
			// given
			Long userId = 1L;
			Long totalAmount = -500L;
			User mockUser = new User(userId, "testName", "test@mail.com", "1234", Point.of(100L));
			given(userRepository.findById(userId)).willReturn(mockUser);

			// when then
			Assertions.assertThatThrownBy(() -> userService.pointPay(userId, totalAmount))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INVALID_PAY_AMOUNT.getMessage());

			verify(userRepository, times(1)).findById(userId);
			verify(userRepository, never()).save(any(User.class));
		}
	}
}