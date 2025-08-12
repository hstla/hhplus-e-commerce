package kr.hhplus.be.server.config.jpa.user.repository;

import kr.hhplus.be.server.config.jpa.user.model.User;

public interface UserRepository {
	User findById(Long userId);
	User save(User user);
	boolean existsByEmail(String email);
	boolean validateUserExists(Long userId);
	User findByIdWithLock(Long userId);
}