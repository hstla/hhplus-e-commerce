package kr.hhplus.be.server.config.jpa.api.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.config.jpa.api.user.controller.dto.user.UserRequest;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.model.User;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserController 통합 테스트")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JpaUserRepository jpaUserRepository;

	@BeforeEach
	void setUp() {
		jpaUserRepository.deleteAll();
	}

	@Nested
	@DisplayName("POST /api/users - 회원 생성")
	class CreateUser {
		@Test
		@DisplayName("회원가입에 성공하고 DB에 저장된다")
		void createUser_success() throws Exception {
			// given
			UserRequest.SignUp request = UserRequest.SignUp.of("테스트유저", "test@mail.com", "12345");

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

			// then
			User savedUser = userRepository.findById(createdUserId);
			assertThat(savedUser.getName()).isEqualTo("테스트유저");
			assertThat(savedUser.getPoint().getAmount()).isEqualTo(0L);
		}
	}

	@Nested
	@DisplayName("GET /api/users/{userId} - 유저 조회")
	class GetUser {
		@Test
		@DisplayName("존재하는 유저의 정보를 반환한다")
		void getUser_success() throws Exception {
			// given
			User user = User.create("userName", "test@email.com", "12345");
			User saved = userRepository.save(user);

			// when then
			mockMvc.perform(get("/api/users/{userId}", saved.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(saved.getId()))
				.andExpect(jsonPath("$.data.name").value("userName"))
				.andExpect(jsonPath("$.data.email").value("test@email.com"));
		}
	}

	@Nested
	@DisplayName("POST /api/users/{userId} - 유저 수정")
	class UpdateUser {
		@Test
		@DisplayName("유저 정보를 수정하고 변경값을 반환한다")
		void updateUser_success() throws Exception {
			// given
			User user = User.create("userName", "test@email.com", "12345");
			User saved = userRepository.save(user);
			String updateName = "updateName";

			UserRequest.Update request = UserRequest.Update.of(updateName);

			// when then
			mockMvc.perform(post("/api/users/{userId}", saved.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value(updateName));

			User updatedUser = userRepository.findById(saved.getId());
			assertThat(updatedUser.getName()).isEqualTo(updateName);
		}
	}
}
