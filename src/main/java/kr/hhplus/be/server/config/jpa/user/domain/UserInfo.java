package kr.hhplus.be.server.config.jpa.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfo {

	@Getter
	@NoArgsConstructor
	public static class Info{
		private Long id;
		private String name;
		private String email;

		private Info(Long id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public static Info of(User user) {
			return new Info(user.getId(), user.getName(), user.getEmail());
		}
	}

	@Getter
	@NoArgsConstructor
	public static class PointInfo{
		private Long id;
		private Long point;

		private PointInfo(Long id, Long point) {
			this.id = id;
			this.point = point;
		}

		public static PointInfo of(User user) {
			return new PointInfo(user.getId(), user.getPointAmount());
		}
	}
}
