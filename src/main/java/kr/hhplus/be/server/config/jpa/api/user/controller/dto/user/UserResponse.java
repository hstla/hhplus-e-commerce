package kr.hhplus.be.server.config.jpa.api.user.dto.user;

import kr.hhplus.be.server.config.jpa.user.domain.model.User;
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

		public static UserInfo of(User user) {
			return new UserInfo(user.getId(), user.getName(), user.getEmail());
		}
	}
}