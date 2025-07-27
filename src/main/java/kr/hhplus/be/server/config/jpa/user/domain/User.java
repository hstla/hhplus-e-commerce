package kr.hhplus.be.server.config.jpa.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

	private static final Long minChargePoint = 1_000L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", unique = true)
	private Long id;
	private String name;
	private String email;
	private String password;
	private Long pointAmount;

	public User(String name, String email, String password, Long pointAmount) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.pointAmount = pointAmount;
	}

	public static User SignUpUser(String name, String email, String password) {
		validateName(name);
		validateEmail(email);
		validatePassword(password);
		return new User(name, email, password,0L);
	}

	public void updateNameAndEmail(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public void chargePoint(Long pointAmount) {
		pointRange(pointAmount);
		this.pointAmount += pointAmount;
	}

	private void pointRange(Long pointAmount) {
		if (pointAmount < minChargePoint) {
			throw new RestApiException(UserErrorCode.INVALID_USER_POINT);
		}
	}

	private static void validateName(String name) {
		if (name == null || name.length() < 2 || name.length() > 10) {
			throw new RestApiException(UserErrorCode.INVALID_USER_NAME);
		}
	}

	private static void validateEmail(String email) {
		if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			throw new RestApiException(UserErrorCode.INVALID_USER_EMAIL);
		}
	}

	private static void validatePassword(String password) {
		if (password == null || password.length() < 5 || password.length() > 20) {
			throw new RestApiException(UserErrorCode.INVALID_USER_PASSWORD);
		}
	}

	public void usePoint(Long totalPrice) {
		if (this.pointAmount < totalPrice) {
			throw new RestApiException(UserErrorCode.INSUFFICIENT_USER_POINT);
		}
		this.pointAmount -= totalPrice;
	}
}