package kr.hhplus.be.server.config.jpa.api.user.usecase.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCommand {

	public record SignUp (
		String name,
		String email,
		String password
	) {
		public static SignUp of(String name, String email, String password) {
			return new SignUp(name, email, password);
		}
	}

	public record Update (
		String name
	) {
		public static Update of(String name) {
			return new Update(name);
		}
	}
}
