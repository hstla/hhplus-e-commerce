package kr.hhplus.be.server.config.jpa.user.repository;

import kr.hhplus.be.server.config.jpa.user.model.User;

public interface UserRepository {
	User findById(Long userId);
	User save(User user);
	User findByEmail(String email);
	boolean existsByEmail(String email);
	User findByIdWithLock(Long userId);
}