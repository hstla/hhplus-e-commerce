package kr.hhplus.be.server.config.jpa.user.infrastructure;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.TestcontainersConfig;
import kr.hhplus.be.server.config.jpa.user.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({UserCoreRepository.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("UserCoreRepository 테스트")
class UserCoreRepositoryTest {

	@Autowired
	private JpaUserRepository jpaUserRepository;

	@Autowired
	private UserCoreRepository userCoreRepository;

	@BeforeEach
	void globalSetUp() {
		jpaUserRepository.deleteAll();
	}

	@Nested
	@DisplayName("existsByEmail 메서드 테스트")
	class ExistsByEmailTests {

		// private User savedUser;

		@BeforeEach
		void setUp() {
			User testUser = User.create("TestUser", "test@example.com", "password123");
			testUser.chargePoint(1_000L);
			jpaUserRepository.save(testUser);
		}

		@Test
		@DisplayName("존재하는 이메일로 조회 시 User 도메인 모델을 반환해야 한다")
		void existsByEmail_success() {
			// given
			String existingEmail = "test@example.com";

			// when
			boolean exists = userCoreRepository.existsByEmail(existingEmail);

			// then
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("존재하지 않는 이메일로 조회 시 INACTIVE_USER 에러를 반환해야 한다")
		void existsByEmail_fail() {
			// given
			String nonExistentEmail = "noEmail@test.com";

			// when then
			boolean exists = userCoreRepository.existsByEmail(nonExistentEmail);

			// then
			assertThat(exists).isFalse();
		}
	}
}