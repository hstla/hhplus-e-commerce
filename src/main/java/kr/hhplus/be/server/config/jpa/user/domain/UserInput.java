package kr.hhplus.be.server.config.jpa.user.domain;

import kr.hhplus.be.server.config.jpa.user.application.UserCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInput {

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

		public static UserInput.SignUp of(String name, String email, String password) {
			return new UserInput.SignUp(name, email, password);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class Update {
		private Long id;
		private String name;
		private String email;

		private Update(Long id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public static UserInput.Update of(Long id, String name, String email) {
			return new UserInput.Update(id, name, email);
		}
	}
}
