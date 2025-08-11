package kr.hhplus.be.server.config.jpa.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserPointResult;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("FindUserPointUseCase 통합 테스트")
class FindUserPointUseCaseTest {

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
			assertAll(
				() ->assertThat(userPoint.userId()).isEqualTo(findUserId),
				() -> assertThat(userPoint.point()).isEqualTo(0L),
				// DB
				() ->assertThat(findUser.getId()).isEqualTo(findUserId),
				() -> assertThat(findUser.getPoint().getAmount()).isEqualTo(0L)
			);
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