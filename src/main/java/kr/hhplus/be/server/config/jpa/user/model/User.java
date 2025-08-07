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
	private static final int MIN_NAME_LENGTH = 2;
	private static final int MAX_NAME_LENGTH = 20;
	private static final int MIN_PASSWORD_LENGTH = 5;
	private static final int MAX_PASSWORD_LENGTH = 30;

	private Long id;
	private String name;
	private String email;
	private String password;
	private Point point;

	public static User signUpUser(String name, String email, String password) {
		validateName(name);
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
		if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
			throw new RestApiException(UserErrorCode.INVALID_USER_NAME);
		}
	}

	private static void validatePassword(String password) {
		if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
			throw new RestApiException(UserErrorCode.INVALID_USER_PASSWORD);
		}
	}
}