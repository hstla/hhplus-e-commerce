package kr.hhplus.be.server.config.jpa.user.interfaces.dto.user;

import kr.hhplus.be.server.config.jpa.user.application.UserResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

	@Getter
	@NoArgsConstructor
	public static class User{
		private Long id;
		private String name;
		private String email;

		private User(Long id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public static User of(UserResult.User user) {
			return new User(user.getId(), user.getName(), user.getEmail());
		}
	}
}