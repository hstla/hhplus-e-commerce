package kr.hhplus.be.server.config.jpa.api.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {

	@Getter
	@NoArgsConstructor
	public static class SignUp {
		@NotBlank
		private String name;
		@NotNull @Email
		private String email;
		@NotBlank
		private String password;

		public SignUp(String name, String email, String password) {
			this.name = name;
			this.email = email;
			this.password = password;
		}

		private static SignUp of(String name, String email, String password) {
			return new SignUp(name, email, password);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class Update {
		@NotBlank
		private String name;
		@NotNull @Email
		private String email;

		public Update(String name, String email) {
			this.name = name;
			this.email = email;
		}

		private static Update of(String name, String email) {
			return new Update(name, email);
		}
	}
}