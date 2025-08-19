package kr.hhplus.be.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.api.user.usecase.dto.UserPointResult;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;

@DisplayName("ChargeUserPointUseCase 통합 테스트")
class ChargeUserPointUseCaseTest extends IntegrationTestConfig {

    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private ChargeUserPointUseCase userPointUseCase;

    private User savedUser;

    @BeforeEach
    void setUp() {
		userRepository.deleteAll();

        User user = User.create("testUser", "testuser@mail.com", "password");
        savedUser = userRepository.save(user);
    }

    @Nested
    @DisplayName("유저 포인트 충전")
    class ChargePointTest {

        @Test
        @DisplayName("포인트 충전에 성공한다")
        void execute() {
            // given
            Long chargePoint = 1_000L;

            // When
            UserPointResult.UserPoint userPoint = userPointUseCase.execute(savedUser.getId(), chargePoint);

			// Then
			User findUser = userRepository.findById(savedUser.getId()).get();
			assertSoftly(soft -> {
				soft.assertThat(userPoint.userId()).isEqualTo(savedUser.getId());
				soft.assertThat(userPoint.point()).isEqualTo(chargePoint);
				// DB
				soft.assertThat(findUser.getId()).isEqualTo(savedUser.getId());
				soft.assertThat(findUser.getPoint().getAmount()).isEqualTo(chargePoint);
			});
        }

        @ParameterizedTest
		@ValueSource(longs = {999L, 1_000_001L})
        @DisplayName("포인트 충전 범위 이하거나 초과하여 포인트 충전에 실패한다")
        void execute_fail_invalidUserPoint(long chargePoint) {
			// then
            // When Then
            assertThatThrownBy(() -> userPointUseCase.execute(savedUser.getId(), chargePoint))
                .isInstanceOf(RestApiException.class)
                .hasMessage(UserErrorCode.INVALID_USER_POINT.getMessage());
        }
    }
}