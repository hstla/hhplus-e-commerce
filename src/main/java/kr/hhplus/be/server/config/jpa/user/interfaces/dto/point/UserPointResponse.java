package kr.hhplus.be.server.config.jpa.user.interfaces.dto.point;

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

		public static UserPoint of(Long userId, Long userPoint) {
			return new UserPoint(userId, userPoint);
		}
	}
}
