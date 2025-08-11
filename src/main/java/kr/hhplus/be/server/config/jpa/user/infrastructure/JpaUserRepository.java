package kr.hhplus.be.server.config.jpa.user.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.config.jpa.user.model.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT us FROM User us WHERE us.id = :id")
	Optional<User> findByIdWithLock(@Param("id") Long userId);
}