package kr.hhplus.be.server.config.jpa.api.user.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {

	public record SignUp (
		@NotBlank String name,
		@NotNull @Email String email,
		@NotBlank String password
	) {
		public static SignUp of(String name, String email, String password) {
			return new SignUp(name, email, password);
		}

		public UserCommand.SignUp toCommand() {
			return UserCommand.SignUp.of(name, email, password);
		}
	}

	public record Update (
		@NotBlank String name
	) {
		public static Update of(String name) {
			return new Update(name);
		}

		public UserCommand.Update toCommand() {
			return UserCommand.Update.of(name);
		}
	}
}