package kr.hhplus.be.server.config.jpa.user.infrastructure;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.model.User;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCoreRepository implements UserRepository {

	private final JpaUserRepository jpaUserRepository;

	@Override
	public User findById(Long userId) {
		return jpaUserRepository.findById(userId)
			.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));
	}

	@Override
	public User save(User user) {
		return jpaUserRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		return jpaUserRepository.findByEmail(email)
			.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));
	}

	@Override
	public boolean existsByEmail(String email) {
		return jpaUserRepository.existsByEmail(email);
	}

	@Override
	public User findByIdWithLock(Long userId) {
		return jpaUserRepository.findByIdWithLock(userId)
			.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));
	}
}