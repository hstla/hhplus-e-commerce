package kr.hhplus.be.server.config.jpa.api.user.controller.dto.point;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserPointResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPointResponse {

	@Getter
	@NoArgsConstructor
	public static class UserPoint{
		private Long userId;
		private Long point;

		private UserPoint(Long userId, Long point) {
			this.userId = userId;
			this.point = point;
		}

		public static UserPoint of(UserPointResult.UserPoint user) {
			return new UserPoint(user.getUserId(), user.getPoint());
		}
	}
}