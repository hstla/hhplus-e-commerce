package kr.hhplus.be.server.config.jpa.api.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.config.jpa.api.user.controller.dto.user.UserRequest;
import kr.hhplus.be.server.config.jpa.api.user.usecase.FindUserUseCase;
import kr.hhplus.be.server.config.jpa.api.user.usecase.SignUpUserUseCase;
import kr.hhplus.be.server.config.jpa.api.user.usecase.UpdateUserUseCase;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserResult;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockitoBean
	private SignUpUserUseCase signUpUserUseCase;
	@MockitoBean
	private UpdateUserUseCase updateUserUseCase;
	@MockitoBean
	private FindUserUseCase findUserUseCase;

	@Nested
	@DisplayName("POST /api/users - 회원 생성")
	class CreateUser {
		@Test
		@DisplayName("회원가입에 성공한다")
		void createUser_success() throws Exception {
			// given
			UserRequest.SignUp request = UserRequest.SignUp.of("테스트유저", "test@mail.com", "12345");
			UserResult.UserInfo userInfo = new UserResult.UserInfo(1L, "테스트유저", "test@mail.com");
			given(signUpUserUseCase.execute(any())).willReturn(userInfo);

			// when
			String responseBody = mockMvc.perform(post("/api/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString(StandardCharsets.UTF_8);

			CommonResponse<Map<String, Object>> response = objectMapper.readValue(responseBody, new TypeReference<>() {});
			Long createdUserId = ((Number) response.getData().get("id")).longValue();
			assertThat(createdUserId).isEqualTo(1L);
		}

		@ParameterizedTest
		@MethodSource("invalidSignUpRequests")
		@DisplayName("유효하지 않은 요청값으로 회원가입에 실패한다")
		void createUser_fail(UserRequest.SignUp request) throws Exception {
			// when then
			mockMvc.perform(post("/api/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
		}

		static Stream<Arguments> invalidSignUpRequests() {
			return Stream.of(
				Arguments.of(UserRequest.SignUp.of(null, "test@mail.com", "12345")),
				Arguments.of(UserRequest.SignUp.of("", "test@mail.com", "12345")),
				Arguments.of(UserRequest.SignUp.of("  ", "test@mail.com", "12345")),
				Arguments.of(UserRequest.SignUp.of("테스트유저", null, "12345")),
				Arguments.of(UserRequest.SignUp.of("테스트유저", "not-an-email", "12345")),
				Arguments.of(UserRequest.SignUp.of("테스트유저", "test@mail.com", null)),
				Arguments.of(UserRequest.SignUp.of("테스트유저", "test@mail.com", "")),
				Arguments.of(UserRequest.SignUp.of("테스트유저", "test@mail.com", "  "))
			);
		}
	}

	@Nested
	@DisplayName("GET /api/users/{userId} - 유저 조회")
	class GetUser {
		@Test
		@DisplayName("존재하는 유저의 정보를 반환한다")
		void getUser_success() throws Exception {
			// given
			Long userId = 1L;
			UserResult.UserInfo userInfo = new UserResult.UserInfo(userId, "userName", "test@email.com");
			given(findUserUseCase.execute(userId)).willReturn(userInfo);

			// when then
			mockMvc.perform(get("/api/users/{userId}", userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(userId))
				.andExpect(jsonPath("$.data.name").value("userName"))
				.andExpect(jsonPath("$.data.email").value("test@email.com"));
		}

		@Test
		@DisplayName("유저 아이디가 음수이므로 테스트에 실패한다")
		void getUser_fail() throws Exception {
			// given
			Long userId = -1L;
			UserResult.UserInfo userInfo = new UserResult.UserInfo(userId, "userName", "test@email.com");
			given(findUserUseCase.execute(userId)).willReturn(userInfo);

			// when then
			mockMvc.perform(get("/api/users/{userId}", userId))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("POST /api/users/{userId} - 유저 수정")
	class UpdateUser {
		@Test
		@DisplayName("유저 정보를 수정하고 변경값을 반환한다")
		void updateUser_success() throws Exception {
			// given
			Long userId = 1L;
			String updateName = "updateName";
			UserRequest.Update request = UserRequest.Update.of(updateName);
			UserResult.UserInfo updatedUserInfo = new UserResult.UserInfo(userId, updateName, "test@email.com");
			given(updateUserUseCase.execute(eq(userId), any())).willReturn(updatedUserInfo);


			// when then
			mockMvc.perform(post("/api/users/{userId}", userId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value(updateName));
		}

		@Test
		@DisplayName("유효하지 않은 이름으로 유저 정보 수정에 실패한다")
		void updateUser_fail() throws Exception {
			// given
			Long userId = 1L;
			UserRequest.Update request = UserRequest.Update.of(" ");

			// when then
			mockMvc.perform(post("/api/users/{userId}", userId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
		}
	}
}
