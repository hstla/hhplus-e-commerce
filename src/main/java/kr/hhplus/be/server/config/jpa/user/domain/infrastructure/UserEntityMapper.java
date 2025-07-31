package kr.hhplus.be.server.config.jpa.user.domain.infrastructure;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.user.domain.model.Point;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;

@Component
public class UserEntityMapper {

	public UserEntity toEntity(User user) {
		if (user == null) {
			return null;
		}

		return new UserEntity(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getPassword(),
			user.getPoint().getAmount()
		);
	}

	public User toUserModel(UserEntity entity) {
		if (entity == null) {
			return null;
		}

		return new User(
			entity.getId(),
			entity.getName(),
			entity.getEmail(),
			entity.getPassword(),
			Point.of(entity.getPointAmount())
		);
	}
}