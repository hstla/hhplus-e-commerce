package kr.hhplus.be.server.config.jpa.user.domain.infrastructure;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import java.util.Optional;

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
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({UserCoreRepository.class, UserEntityMapper.class, TestcontainersConfig.class})
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
	@DisplayName("findById 메서드 테스트")
	class FindByIdTests {

		private UserEntity savedUserEntity;

		@BeforeEach
		void setUp() {
			UserEntity testEntity = new UserEntity("TestUser", "test@example.com", "password123", 1_000L);
			savedUserEntity = jpaUserRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 ID로 조회 시 User 도메인 모델을 반환해야 한다")
		void findById_success() {
			// when
			User foundUser = userCoreRepository.findById(savedUserEntity.getId());

			// then
			assertThat(foundUser.getId()).isEqualTo(savedUserEntity.getId());
			assertThat(foundUser.getName()).isEqualTo("TestUser");
			assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
			assertThat(foundUser.getPassword()).isEqualTo("password123");
			assertThat(foundUser.getPoint().getAmount()).isEqualTo(1_000L);
		}

		@Test
		@DisplayName("존재하지 않는 ID로 조회 시 INACTIVE_USER 에러를 반환한다")
		void findById_fail_inactiveUser() {
			// given
			Long nonExistentId = 999L;

			// when then
			assertThatThrownBy(() -> userCoreRepository.findById(nonExistentId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INACTIVE_USER.getMessage());
		}
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		@Test
		@DisplayName("새로운 User 도메인 모델을 성공적으로 저장해야 한다")
		void save_success() {
			// given
			User newUser = User.signUpUser("NewUser", "new@example.com", "password123");

			// when
			User savedUser = userCoreRepository.save(newUser);

			// then
			assertThat(savedUser).isNotNull();
			assertThat(savedUser.getId()).isNotNull();
			assertThat(savedUser.getName()).isEqualTo("NewUser");
			assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
			assertThat(savedUser.getPassword()).isEqualTo("password123");
			assertThat(savedUser.getPoint().getAmount()).isEqualTo(0L);

			Optional<UserEntity> entityInDb = jpaUserRepository.findById(savedUser.getId());
			assertThat(entityInDb).isPresent();
			assertThat(entityInDb.get().getEmail()).isEqualTo("new@example.com");
		}

		@Test
		@DisplayName("기존 User 도메인 모델의 이름과 포인트 충전을 성공적으로 업데이트해야 한다")
		void save_update() {
			// given
			UserEntity existingEntity = new UserEntity("oldName", "test@example.com", "testPassword", 1_000L);
			UserEntity savedOriginalEntity = jpaUserRepository.save(existingEntity);

			User userToUpdate = userCoreRepository.findById(savedOriginalEntity.getId());
			userToUpdate.updateName("UpdatedName");
			userToUpdate.chargePoint(1_000L);

			// when
			User updatedUser = userCoreRepository.save(userToUpdate);

			// then
			assertThat(updatedUser).isNotNull();
			assertThat(updatedUser.getId()).isEqualTo(savedOriginalEntity.getId());
			assertThat(updatedUser.getName()).isEqualTo("UpdatedName");
			assertThat(updatedUser.getEmail()).isEqualTo("test@example.com");
			assertThat(updatedUser.getPassword()).isEqualTo("testPassword");
			assertThat(updatedUser.getPoint().getAmount()).isEqualTo(2_000L);
		}
	}

	@Nested
	@DisplayName("findByEmail 메서드 테스트")
	class FindByEmailTests {

		private UserEntity savedUserEntity;

		@BeforeEach
		void setUp() {
			UserEntity testEntity = new UserEntity("EmailUser", "email@test.com", "emlpass", 1_000L);
			savedUserEntity = jpaUserRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 이메일로 조회 시 User 도메인 모델을 반환해야 한다")
		void findByEmail_success() {
			// given
			String existingEmail = "email@test.com";

			// when
			User foundUser = userCoreRepository.findByEmail(existingEmail);

			// then
			assertThat(foundUser.getName()).isEqualTo("EmailUser");
			assertThat(foundUser.getEmail()).isEqualTo(existingEmail);
			assertThat(foundUser.getId()).isEqualTo(savedUserEntity.getId());
		}

		@Test
		@DisplayName("존재하지 않는 이메일로 조회 시 INACTIVE_USER 에러를 반환해야 한다")
		void findByEmail_fail() {
			// given
			String nonExistentEmail = "noEmail@test.com";

			// when then
			assertThatThrownBy(() -> userCoreRepository.findByEmail(nonExistentEmail))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INACTIVE_USER.getMessage());
		}
	}

	@Nested
	@DisplayName("existsByEmail 메서드 테스트")
	class ExistsByEmailTests {

		private UserEntity savedUserEntity;

		@BeforeEach
		void setUp() {
			UserEntity testEntity = new UserEntity("EmailUser", "email@test.com", "emlpass", 0L);
			savedUserEntity = jpaUserRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 이메일로 조회 시 User 도메인 모델을 반환해야 한다")
		void existsByEmail_success() {
			// given
			String existingEmail = "email@test.com";

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