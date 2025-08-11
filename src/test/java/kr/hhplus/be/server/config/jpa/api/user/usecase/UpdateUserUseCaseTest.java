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

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserCommand;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserResult;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UpdateUserUseCase 통합 테스트")
class UpdateUserUseCaseTest {

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
            assertAll(
                () -> assertThat(userInfo.name()).isEqualTo(newName),
				// DB
                () -> assertThat(updatedUser.getName()).isEqualTo(newName)
            );
        }

        @Test
        @DisplayName("수정하려는 유저가 존재하지 않아 실패한다")
        void updateUser_fail_userNotFound() {
            // given
            Long notExistUserId = 999L;
            UserCommand.Update command = new UserCommand.Update("newName");

            // when & then
            RestApiException exception = assertThrows(RestApiException.class, () -> {
                updateUserUseCase.execute(notExistUserId, command);
            });
            assertEquals(UserErrorCode.INACTIVE_USER, exception.getErrorCode());
        }
    }
}