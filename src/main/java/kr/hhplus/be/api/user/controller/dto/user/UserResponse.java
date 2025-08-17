package kr.hhplus.be.api.user.controller.dto.user;

import kr.hhplus.be.api.user.usecase.dto.UserResult;
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