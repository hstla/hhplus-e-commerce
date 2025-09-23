package kr.hhplus.be.application.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.application.user.dto.UserCommand;
import kr.hhplus.be.application.user.dto.UserResult;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import kr.hhplus.be.infrastructure.persistence.user.JpaUserRepository;

@DisplayName("UpdateUserUseCase 통합 테스트")
class UpdateUserUseCaseTest extends IntegrationTestConfig {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private UpdateUserUseCase updateUserUseCase;

    private User savedUser;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
        User user = User.create("testUser", "testuser@mail.com", "password");
        savedUser = jpaUserRepository.save(user);
    }

    @Nested
    @DisplayName("유저 이름 수정")
    class UpdateUserName {

        @Test
        @DisplayName("유저 이름 수정에 성공한다")
        void updateUser_success() {
            // given
            String newName = "newName";
            UserCommand.Update command = new UserCommand.Update(newName);

            // when
            UserResult.UserInfo userInfo = updateUserUseCase.execute(savedUser.getId(), command);

            // then
            User updatedUser = jpaUserRepository.findById(savedUser.getId()).get();
			assertSoftly(soft -> {
				soft.assertThat(userInfo.name()).isEqualTo(newName);
				// DB
				soft.assertThat(updatedUser.getName()).isEqualTo(newName);
			});
        }

        @Test
        @DisplayName("수정하려는 유저가 존재하지 않아 실패한다")
        void updateUser_fail_userNotFound() {
            // given
            Long notExistUserId = 999L;
            UserCommand.Update command = new UserCommand.Update("newName");

            // when & then
			assertThatThrownBy(() -> updateUserUseCase.execute(notExistUserId, command))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.INACTIVE_USER.getMessage());
        }
    }
}