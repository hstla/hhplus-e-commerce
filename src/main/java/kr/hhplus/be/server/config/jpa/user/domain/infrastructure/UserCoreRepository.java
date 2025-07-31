package kr.hhplus.be.server.config.jpa.user.domain.infrastructure;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCoreRepository implements UserRepository {

	private final JpaUserRepository jpaUserRepository;
	private final UserEntityMapper userMapper;

	@Override
	public User findById(Long userId) {
		return jpaUserRepository.findById(userId).map(userMapper::toUserModel)
			.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));
	}

	@Override
	public User save(User user) {
		UserEntity userEntity = userMapper.toEntity(user);
		UserEntity savedEntity = jpaUserRepository.save(userEntity);
		return userMapper.toUserModel(savedEntity);
	}

	@Override
	public User findByEmail(String email) {
		return jpaUserRepository.findByEmail(email).map(userMapper::toUserModel)
			.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));
	}

	@Override
	public boolean existsByEmail(String email) {
		return jpaUserRepository.existsByEmail(email);
	}
}