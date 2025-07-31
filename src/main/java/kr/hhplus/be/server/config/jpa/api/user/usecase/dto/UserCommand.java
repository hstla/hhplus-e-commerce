package kr.hhplus.be.server.config.jpa.api.user.usecase.dto;

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

		public static SignUp of(String name, String email, String password) {
			return new SignUp(name, email, password);
		}

	}

	@Getter
	@NoArgsConstructor
	public static class Update {
		private String name;

		private Update(String name) {
			this.name = name;
		}

		public static Update of(String name) {
			return new Update(name);
		}
	}

}
