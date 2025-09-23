package kr.hhplus.be.infrastructure.web.controller.user.controller.dto.user;

import kr.hhplus.be.application.user.dto.UserResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

	public record UserInfo (
		long id,
		String name,
		String email
	) {
		public static UserInfo of(UserResult.UserInfo user) {
			return new UserInfo(user.id(), user.name(), user.email());
		}
	}
}