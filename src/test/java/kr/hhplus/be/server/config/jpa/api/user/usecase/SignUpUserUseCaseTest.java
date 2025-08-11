package kr.hhplus.be.server.config.jpa.api.user.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
@DisplayName("SignUpUserUseCase 통합 테스트")
class SignUpUserUseCaseTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private SignUpUserUseCase signUpUserUseCase;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Nested
    @DisplayName("유저 회원가입")
    class SignUpUser {

        @Test
        @DisplayName("유저 회원가입에 성공한다.")
        void signUp_success() {
            // given
            UserCommand.SignUp command = new UserCommand.SignUp("testUser", "test@example.com", "password123");

            // when
            UserResult.UserInfo userInfo = signUpUserUseCase.execute(command);

            // then
            User savedUser = jpaUserRepository.findById(userInfo.id()).get();
            assertAll(
                () -> assertThat(userInfo.id()).isNotNull(),
                () -> assertThat(userInfo.name()).isEqualTo(command.name()),
                () -> assertThat(userInfo.email()).isEqualTo(command.email()),
				// DB
                () -> assertNotNull(savedUser),
                () -> assertThat(savedUser.getId()).isEqualTo(userInfo.id()),
                () -> assertThat(savedUser.getName()).isEqualTo(command.name()),
                () -> assertThat(savedUser.getEmail()).isEqualTo(command.email())
            );
        }

        @Test
        @DisplayName("중복된 이메일로 회원가입에 실패한다.")
        void signUp_fail_duplicateEmail() {
            // given
            User existingUser = User.create("existingUser", "test@example.com", "password");
            jpaUserRepository.save(existingUser);
            UserCommand.SignUp command = new UserCommand.SignUp("newUser", "test@example.com", "password123");

            // when & then
			assertThatThrownBy(() -> signUpUserUseCase.execute(command))
				.isInstanceOf(RestApiException.class)
				.hasMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
        }
    }
}