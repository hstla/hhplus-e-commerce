package kr.hhplus.be.server.config.jpa.api.user.usecase.dto;

import kr.hhplus.be.server.config.jpa.user.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResult {

	public record UserInfo (
		long id,
		String name,
		String email
	) {
		public static UserInfo of(User user) {
			return new UserInfo(user.getId(), user.getName(), user.getEmail());
		}
	}
}