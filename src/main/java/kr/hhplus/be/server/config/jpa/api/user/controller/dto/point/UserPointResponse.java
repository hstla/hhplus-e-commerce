package kr.hhplus.be.server.config.jpa.api.user.dto.point;

import kr.hhplus.be.server.config.jpa.user.domain.model.User;
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

		public static UserPoint of(User user) {
			return new UserPoint(user.getId(), user.getPoint().getAmount());
		}
	}
}