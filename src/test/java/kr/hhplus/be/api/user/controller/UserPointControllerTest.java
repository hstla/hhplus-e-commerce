package kr.hhplus.be.api.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.api.user.controller.dto.point.UserPointRequest;
import kr.hhplus.be.api.user.usecase.ChargeUserPointUseCase;
import kr.hhplus.be.api.user.usecase.FindUserPointUseCase;
import kr.hhplus.be.api.user.usecase.dto.UserPointResult;

@WebMvcTest(UserPointController.class)
@DisplayName("UserPointController 단위 테스트")
class UserPointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FindUserPointUseCase findUserPointUseCase;

	@MockitoBean
    private ChargeUserPointUseCase chargeUserPointUseCase;

    @Nested
    @DisplayName("GET /api/users/{userId}/points - 포인트 조회")
    class GetPoint {
        @Test
        @DisplayName("존재하는 유저의 포인트를 반환한다")
        void getPoint_success() throws Exception {
            // given
            Long userId = 1L;
            long point = 1000L;
            UserPointResult.UserPoint userPoint = new UserPointResult.UserPoint(userId, point);
            given(findUserPointUseCase.execute(userId)).willReturn(userPoint);

            // when // then
            mockMvc.perform(get("/api/users/{userId}/points", userId)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(point));
        }

		@Test
		@DisplayName("유저 아이디가 음수를 입력받아 조회에 실패한다")
		void getPoint_fail() throws Exception {
			// given
			Long userId = -1L;
			long point = 1000L;
			UserPointResult.UserPoint userPoint = new UserPointResult.UserPoint(userId, point);
			given(findUserPointUseCase.execute(userId)).willReturn(userPoint);

			// when // then
			mockMvc.perform(get("/api/users/{userId}/points", userId)
					.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}
    }

    @Nested
    @DisplayName("POST /api/users/{userId}/points - 포인트 충전")
    class ChargePoint {
        @Test
        @DisplayName("포인트 충전에 성공하고 변경된 포인트를 반환한다")
        void chargePoint_success() throws Exception {
            // given
            long userId = 1L;
            long chargeAmount = 5_000L;
            long finalPoint = 6_000L;
            UserPointRequest.ChargePoint request = UserPointRequest.ChargePoint.of(chargeAmount);
            UserPointResult.UserPoint userPoint = new UserPointResult.UserPoint(userId, finalPoint);
            given(chargeUserPointUseCase.execute(anyLong(), anyLong())).willReturn(userPoint);

            // when // then
            mockMvc.perform(post("/api/users/{userId}/points", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(finalPoint));
        }

        @Test
        @DisplayName("유효하지 않은 금액으로 포인트 충전에 실패한다")
        void chargePoint_fail() throws Exception {
            // given
            long userId = 1L;
			long chargeAmount = -1_000L;
            UserPointRequest.ChargePoint request = UserPointRequest.ChargePoint.of(chargeAmount);

            // when // then
            mockMvc.perform(post("/api/users/{userId}/points", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}