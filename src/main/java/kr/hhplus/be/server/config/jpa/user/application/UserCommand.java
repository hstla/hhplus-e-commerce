package kr.hhplus.be.server.config.jpa.user.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCommand {

	@Getter
	@NoArgsConstructor
	public static class SignUp {
		private String name;
		private String email;
		private String password;

		private SignUp(String name, String email, String password) {
			this.name = name;
			this.email = email;
			this.password = password;
		}

		public static UserCommand.SignUp of(String name, String email, String password) {
			return new UserCommand.SignUp(name, email, password);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class User {
		private Long id;
		private String name;
		private String email;

		private User(Long id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public static User of(Long id, String name, String email) {
			return new User(id, name, email);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class update {
		private String name;
		private String email;

		private update(String name, String email) {
			this.name = name;
			this.email = email;
		}

		public static update of(String name, String email) {
			return new update(name, email);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class ChargePoint {
		private Long userId;
		private Long point;

		private ChargePoint(Long userId, Long point) {
			this.userId = userId;
			this.point = point;
		}

		public static ChargePoint of(Long userId, Long point) {
			return new ChargePoint(userId, point);
		}
	}
}
