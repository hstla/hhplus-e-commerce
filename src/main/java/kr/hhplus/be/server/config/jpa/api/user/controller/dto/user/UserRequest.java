package kr.hhplus.be.server.config.jpa.api.user.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserCommand;
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

		private SignUp(String name, String email, String password) {
			this.name = name;
			this.email = email;
			this.password = password;
		}

		public static SignUp of(String name, String email, String password) {
			return new SignUp(name, email, password);
		}

		public UserCommand.SignUp toCommand() {
			return UserCommand.SignUp.of(name, email, password);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class Update {
		@NotBlank
		private String name;

		private Update(String name) {
			this.name = name;
		}

		public static Update of(String name) {
			return new Update(name);
		}

		public UserCommand.Update toCommand() {
			return UserCommand.Update.of(name);
		}
	}
}