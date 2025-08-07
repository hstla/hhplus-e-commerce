package kr.hhplus.be.server.config.jpa.user.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("User 도메인 단위 테스트")
class UserTest {

	@Nested
	@DisplayName("signUpUser 메서드 테스트")
	class SignUpUserTests {

		@Test
		@DisplayName("올바른 값으로 회원가입 성공한다")
		void signUpUser_success() {
			//given
			User user = User.signUpUser("testName", "test@email.com", "password");

			// when then
			assertThat(user).isNotNull();
			assertThat(user.getId()).isNull(); // db에서 id를 생성하므로
			assertThat(user.getName()).isEqualTo("testName");
			assertThat(user.getEmail()).isEqualTo("test@email.com");
			assertThat(user.getPassword()).isEqualTo("password");
			assertThat(user.getPoint().getAmount()).isZero();
		}

		@ParameterizedTest
		@ValueSource(strings = {"a", "abcdefghijklmnopqrs21"})
		@DisplayName("이름길이가 허용 범위를 넘어서면 예외를 발생시킨다")
		void signUpUser_fail_nameTooShort(String name) {
			assertThatThrownBy(() -> User.signUpUser(name, "test@email.com", "password"))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INVALID_USER_NAME.getMessage());
		}

		@ParameterizedTest
		@ValueSource(strings = {"abcd", "abcdefghijklmnopqrstuvwxyz12345"})
		@DisplayName("비밀번호 길이가 허용 범위를 넘어서면 예외를 발생시킨다")
		void signUpUser_fail_passwordTooShort(String password) {
			assertThatThrownBy(() -> User.signUpUser("testName", "test@email.com", password))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INVALID_USER_PASSWORD.getMessage());
		}
	}

	@Nested
	@DisplayName("updateName 메서드 테스트")
	class UpdateNameTests {

		@Test
		@DisplayName("이름 변경이 정상적으로 수행된다")
		void updateName_success() {
			// given
			User user = User.signUpUser("oldName", "test@email.com", "password");
			String newName = "newName";

			// when
			user.updateName(newName);

			// then
			assertThat(user.getName()).isEqualTo(newName);
		}

		@ParameterizedTest
		@ValueSource(strings = {"a", "abcdefghijklmnopqrs21"})
		@DisplayName("이름이 너무 짧거나 길면 예외 발생한다")
		void signUpUser_fail_nameTooShort(String newName) {
			// given
			User user = User.signUpUser("oldName", "test@email.com", "password");

			// when then
			assertThatThrownBy(() -> user.updateName(newName))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INVALID_USER_NAME.getMessage());
		}
	}

	@Nested
	@DisplayName("포인트 충전 및 사용 테스트")
	class PointTests {

		@Test
		@DisplayName("포인트 충전 성공한다")
		void chargePoint_success() {
			// given
			User user = User.signUpUser("testName", "test@email.com", "password");
			long chargeAmount = 1_000L;

			// when
			user.chargePoint(chargeAmount);

			// then
			assertThat(user.getPoint().getAmount()).isEqualTo(chargeAmount);
		}

		@ParameterizedTest
		@ValueSource(longs = {-1L, 999L, 1_000_001L})
		@DisplayName("유효하지 않는 포인트를 충전하여 충전에 실패한다")
		void usePoint_fail_insufficientPoint(long chargeAmount) {
			// given
			User user = User.signUpUser("testName", "test@email.com", "password");

			// when then
			assertThatThrownBy(() -> user.chargePoint(chargeAmount))
				.isInstanceOf(RestApiException.class)
				.hasMessageContaining(UserErrorCode.INVALID_CHARGE_AMOUNT.getMessage());
		}

		@Test
		@DisplayName("포인트 사용 성공한다")
		void usePoint_success() {
			// given
			User user = User.signUpUser("testName", "test@email.com", "password");
			user.chargePoint(2000L);

			// when
			user.usePoint(1500L);

			// then
			assertThat(user.getPoint().getAmount()).isEqualTo(500L);
		}

		@Test
		@DisplayName("보유 포인트보다 많은 포인트를 사용하여 실패한다")
		void usePoint_fail() {
			// given
			User user = User.signUpUser("testName", "test@email.com", "password");
			user.chargePoint(2000L);

			// when then
			assertThatThrownBy(() -> user.usePoint(3_000L))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INSUFFICIENT_USER_POINT.getMessage());
		}
	}
}