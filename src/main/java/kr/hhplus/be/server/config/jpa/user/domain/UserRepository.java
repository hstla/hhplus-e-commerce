package kr.hhplus.be.server.config.jpa.user.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

	boolean existsById(Long userId);

	Optional<User> findById(Long userId);

	User save(User user);

	void deleteById(Long userId);

	void clear();

	List<User> findAll();

	boolean existsByEmail(String email);
}