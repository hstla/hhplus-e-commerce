package kr.hhplus.be.application.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.application.user.dto.UserPointResult;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import kr.hhplus.be.infrastructure.persistence.user.JpaUserRepository;

@DisplayName("FindUserPointUseCase 통합 테스트")
class FindUserPointUseCaseTest extends IntegrationTestConfig {

    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private FindUserPointUseCase findUserPointUseCase;

    private User savedUser;

    @BeforeEach
    void setUp() {
		userRepository.deleteAll();

        User user = User.create("testUser", "testuser@mail.com", "password");
        savedUser = userRepository.save(user);
    }

    @Nested
    @DisplayName("유저 포인트 조회")
    class FindUserPoint {

        @Test
        @DisplayName("사용자를 찾아 포인트 조회에 성공한다")
        void findUser_success() {
			// then
			Long findUserId = savedUser.getId();

			// When
			UserPointResult.UserPoint userPoint = findUserPointUseCase.execute(findUserId);
			User findUser = userRepository.findById(findUserId).get();

			// Then
			assertSoftly(soft -> {
				soft.assertThat(userPoint.userId()).isEqualTo(findUserId);
				soft.assertThat(userPoint.point()).isEqualTo(0L);
				// DB
				soft.assertThat(findUser.getId()).isEqualTo(findUserId);
				soft.assertThat(findUser.getPoint().getAmount()).isEqualTo(0L);
			});
        }

        @Test
        @DisplayName("사용자를 찾을 수 없어 포인트 조회에 실패한다")
        void findUser_fail() {
            // Given
            Long notExistUserId = 999L;

            // When Then
            assertThatThrownBy(() -> findUserPointUseCase.execute(notExistUserId))
                .isInstanceOf(RestApiException.class)
                .hasMessage(UserErrorCode.INACTIVE_USER.getMessage());
        }
    }
}