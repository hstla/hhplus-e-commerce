package kr.hhplus.be.server.config.jpa.user.domain.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface JpaUserRepository extends JpaRepository<UserEntity,Long> {
	Optional<UserEntity> findByEmail(String email);
	boolean existsByEmail(String email);
}