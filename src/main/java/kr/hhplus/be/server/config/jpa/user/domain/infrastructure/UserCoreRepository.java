package kr.hhplus.be.server.config.jpa.user.domain.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@Repository
public class UserCoreRepositoryImpl implements UserRepository {




	@Override
	public boolean existsById(Long userId) {
		return false;
	}

	@Override
	public Optional<User> findById(Long userId) {
		return Optional.empty();
	}

	@Override
	public User save(User user) {
		return null;
	}

	@Override
	public void deleteById(Long userId) {

	}

	@Override
	public void clear() {

	}

	@Override
	public List<User> findAll() {
		return List.of();
	}

	@Override
	public boolean existsByEmail(String email) {
		return false;
	}
}
