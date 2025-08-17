package kr.hhplus.be.domain.user.repository;

import kr.hhplus.be.domain.user.model.User;

public interface UserRepository {
	User findById(Long userId);
	User save(User user);
	boolean existsByEmail(String email);
	boolean assertUserExists(Long userId);
	User findByIdWithLock(Long userId);
}