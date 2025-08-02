package kr.hhplus.be.server.config.jpa.api.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.config.jpa.api.user.controller.dto.point.UserPointRequest;
import kr.hhplus.be.server.config.jpa.user.domain.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserPointController 통합 테스트")
class UserPointControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JpaUserRepository jpaUserRepository;
	@Autowired
	private UserRepository UserRepository;

	private User savedUser;

	@BeforeEach
	void setUp() {
		jpaUserRepository.deleteAll();
		User user = User.signUpUser("testUser", "testuser@mail.com", "password");
		savedUser = UserRepository.save(user);
	}

	@Nested
	@DisplayName("GET /api/users/{userId}/points - 포인트 조회")
	class GetPoint {
		@Test
		@DisplayName("존재하는 유저의 포인트를 반환한다")
		void getPoint_success() throws Exception {
			mockMvc.perform(get("/api/users/{userId}/points", savedUser.getId())
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.point").value(savedUser.getPoint().getAmount()));
		}
	}

	@Nested
	@DisplayName("POST /api/users/{userId}/points - 포인트 충전")
	class ChargePoint {
		@Test
		@DisplayName("포인트 충전에 성공하고 변경된 포인트를 반환한다")
		void chargePoint_success() throws Exception {
			long chargePoint = 5_000L;
			UserPointRequest.ChargePoint request = UserPointRequest.ChargePoint.of(chargePoint);

			mockMvc.perform(post("/api/users/{userId}/points", savedUser.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.point").value(chargePoint))
				.andReturn()
				.getResponse();
		}
	}
}