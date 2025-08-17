package kr.hhplus.be.api.user.controller.dto.point;

import kr.hhplus.be.api.user.usecase.dto.UserPointResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPointResponse {

	public record UserPoint(Long userId, Long point) {
		public static UserPoint of(UserPointResult.UserPoint user) {
			return new UserPoint(user.userId(), user.point());
		}
	}
}