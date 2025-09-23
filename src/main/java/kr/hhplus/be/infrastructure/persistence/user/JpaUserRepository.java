package kr.hhplus.be.infrastructure.persistence.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.domain.user.model.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT us FROM User us WHERE us.id = :id")
	Optional<User> findByIdWithLock(@Param("id") Long userId);
}