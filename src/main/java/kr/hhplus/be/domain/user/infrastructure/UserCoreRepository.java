package kr.hhplus.be.domain.user.infrastructure;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
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
	public boolean existsByEmail(String email) {
		return jpaUserRepository.existsByEmail(email);
	}

	@Override
	public User findByIdWithLock(Long userId) {
		return jpaUserRepository.findByIdWithLock(userId)
			.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));
	}

	@Override
	public boolean assertUserExists(Long userId) {
		if (!jpaUserRepository.existsById(userId)) {
			throw new RestApiException(UserErrorCode.INACTIVE_USER);
		}
		return true;
	}
}