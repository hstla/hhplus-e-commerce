package kr.hhplus.be.infrastructure.web.controller.user.controller.dto.point;

import kr.hhplus.be.application.user.dto.UserPointResult;
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