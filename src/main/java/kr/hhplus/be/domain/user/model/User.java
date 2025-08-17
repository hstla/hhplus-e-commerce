package kr.hhplus.be.domain.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.global.common.BaseEntity;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User extends BaseEntity {
	private static final int MIN_NAME_LENGTH = 2;
	private static final int MAX_NAME_LENGTH = 20;
	private static final int MIN_PASSWORD_LENGTH = 5;
	private static final int MAX_PASSWORD_LENGTH = 30;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name", length = 20, nullable = false)
	private String name;
	@Column(name = "email", length = 50, nullable = false, unique = true)
	private String email;
	@Column(name = "password", nullable = false)
	private String password;
	@Embedded
	private Point point;

	public static User create(String name, String email, String password) {
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