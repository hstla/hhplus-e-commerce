package kr.hhplus.be.server.config.jpa.api.user.controller.dto.user;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

	@Getter
	@NoArgsConstructor
	public static class UserInfo {
		private Long id;
		private String name;
		private String email;

		private UserInfo(Long id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public static UserInfo of(UserResult.UserInfo user) {
			return new UserInfo(user.getId(), user.getName(), user.getEmail());
		}
	}
}