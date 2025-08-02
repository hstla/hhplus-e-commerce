package kr.hhplus.be.server.config.jpa.user.domain.model;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
	private Long id;
	private String name;
	private String email;
	private String password;
	private Point point;

	public static User signUpUser(String name, String email, String password) {
		validateName(name);
		validateEmail(email);
		validatePassword(password);
		return new User(null, name, email, password, Point.zero());
	}

	public void updateName(String name) {
		validateName(name);
		this.name = name;
	}

	public void chargePoint(Long amount) {
		this.point = this.point.charge(amount);
	}

	public void usePoint(Long totalPrice) {
		this.point = this.point.use(totalPrice);
	}

	private static void validateName(String name) {
		if (name == null || name.length() < 2 || name.length() > 15) {
			throw new RestApiException(UserErrorCode.INVALID_USER_NAME);
		}
	}

	private static void validateEmail(String email) {
		if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			throw new RestApiException(UserErrorCode.INVALID_USER_EMAIL);

		}
	}

	private static void validatePassword(String password) {
		if (password == null || password.length() < 5 || password.length() > 30) {
			throw new RestApiException(UserErrorCode.INVALID_USER_PASSWORD);
		}
	}
}