package kr.hhplus.be.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.api.user.usecase.dto.UserResult;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;

@DisplayName("FindUserUseCase 통합 테스트")
class FindUserUseCaseTest extends IntegrationTestConfig {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private FindUserUseCase findUserUseCase;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Nested
    @DisplayName("유저 조회")
    class FindUser {

        @Test
        @DisplayName("사용자를 찾아 조회에 성공한다")
        void findUser_success() {
            // given
            User user = User.create("testUser", "testuser@mail.com", "password");
            User savedUser = jpaUserRepository.save(user);

            // when
            UserResult.UserInfo userInfo = findUserUseCase.execute(savedUser.getId());

			// then
			User findUser = jpaUserRepository.findById(savedUser.getId()).get();
			assertSoftly(soft -> {
				soft.assertThat(savedUser.getId()).isEqualTo(userInfo.id());
				soft.assertThat(savedUser.getName()).isEqualTo(userInfo.name());
				soft.assertThat(savedUser.getEmail()).isEqualTo(userInfo.email());
				// DB
				soft.assertThat(findUser.getId()).isEqualTo(userInfo.id());
				soft.assertThat(findUser.getName()).isEqualTo(userInfo.name());
				soft.assertThat(findUser.getEmail()).isEqualTo(userInfo.email());
			});
        }

        @Test
        @DisplayName("사용자를 찾을 수 없어 조회에 실패한다")
        void findUser_fail() {
            // given
            Long notExistUserId = 999L;

            // when & then
			assertThatThrownBy(() -> findUserUseCase.execute(notExistUserId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INACTIVE_USER.getMessage());
        }
    }
}
