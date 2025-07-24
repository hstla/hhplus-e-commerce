package kr.hhplus.be.server.config.jpa.user.application;

import kr.hhplus.be.server.config.jpa.user.domain.UserInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResult {

	@Getter
	@NoArgsConstructor
	public static class User {
		private Long id;
		private String name;
		private String email;

		private User(Long id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public static User of(UserInfo.Info info) {
			return new User(info.getId(), info.getName(), info.getEmail());
		}
	}

	@Getter
	@NoArgsConstructor
	public static class UserPoint {
		private Long id;
		private Long point;

		private UserPoint(Long id, Long point) {
			this.id = id;
			this.point = point;
		}

		public static UserPoint of(Long userId, Long point) {
			return new UserPoint(userId, point);
		}
	}
}
