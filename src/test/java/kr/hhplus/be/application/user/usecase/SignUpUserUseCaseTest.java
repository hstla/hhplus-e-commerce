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

@DisplayName("SignUpUserUseCase 통합 테스트")
class SignUpUserUseCaseTest extends IntegrationTestConfig {

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
			assertSoftly(soft -> {
				soft.assertThat(userInfo.id()).isNotNull();
				soft.assertThat(userInfo.name()).isEqualTo(command.name());
				soft.assertThat(userInfo.email()).isEqualTo(command.email());
				// DB
				soft.assertThat(savedUser).isNotNull();
				soft.assertThat(savedUser.getId()).isEqualTo(userInfo.id());
				soft.assertThat(savedUser.getName()).isEqualTo(command.name());
				soft.assertThat(savedUser.getEmail()).isEqualTo(command.email());
			});
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