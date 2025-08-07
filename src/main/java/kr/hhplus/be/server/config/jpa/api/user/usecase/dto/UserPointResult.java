package kr.hhplus.be.server.config.jpa.api.user.usecase.dto;

import kr.hhplus.be.server.config.jpa.user.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPointResult {

	public record UserPoint (
		long userId,
		long point
	) {
		public static UserPoint of(User user) {
			return new UserPoint(user.getId(), user.getPoint().getAmount());
		}
	}
}
