package kr.hhplus.be.api.usercoupon.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.api.usercoupon.controller.dto.UserCouponRequest;
import kr.hhplus.be.api.usercoupon.usecase.FindUserCouponUseCase;
import kr.hhplus.be.api.usercoupon.usecase.QueuePublishCouponUseCase;
import kr.hhplus.be.api.usercoupon.usecase.dto.UserCouponResult;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.usercoupon.model.UserCouponStatus;

@WebMvcTest(UserCouponController.class)
@DisplayName("UserCouponController 단위 테스트")
class UserCouponControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockitoBean
	private FindUserCouponUseCase findUserCouponUseCase;
	@MockitoBean
	private QueuePublishCouponUseCase queuePublishCouponUseCase;

	private final Long testUserId = 1L;
	private final Long testCouponId1 = 101L;

	@Nested
	@DisplayName("POST /api/coupons/publish - 쿠폰 발행")
	class PublishCoupon {

		@Test
		@DisplayName("쿠폰 발행 성공")
		void publishCoupon_success() throws Exception {
			// given
			UserCouponRequest.Publish request = UserCouponRequest.Publish.of(testUserId, testCouponId1);

			// when & then
			mockMvc.perform(post("/api/coupons/publish")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isAccepted());
		}

		@ParameterizedTest
		@MethodSource("invalidNameProvider")
		@DisplayName("userId, couponId가 -1이하 또는 null이면 에러가 발생한다")
		void create_invalid_name(Long userId, Long couponId) throws Exception {
			// given
			var request = new UserCouponRequest.Publish(userId, couponId);

			// when
			mockMvc.perform(post("/api/coupons/publish")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}

		private static Stream<Arguments> invalidNameProvider() {
			return Stream.of(
				Arguments.of(null, null),
				Arguments.of(-1L, -1L)
			);
		}
	}

	@Nested
	@DisplayName("GET /api/coupons/{userId} - 사용자 쿠폰 조회")
	class GetUserCoupon {

		@Test
		@DisplayName("쿠폰 조회 성공")
		void getUserCoupon_success() throws Exception {
			// given
			UserCouponResult.UserCouponInfo coupon1 = new UserCouponResult.UserCouponInfo(11L, UserCouponStatus.ISSUED, null, 12L, "1000원 할인 쿠폰"
				,CouponType.FIXED,  1000L, LocalDateTime.now().plusDays(1));
			UserCouponResult.UserCouponInfo coupon2 = new UserCouponResult.UserCouponInfo(11L, UserCouponStatus.ISSUED, null, 12L, "2000원 할인 쿠폰"
				,CouponType.FIXED,  2000L, LocalDateTime.now().plusDays(1));

			given(findUserCouponUseCase.execute(any())).willReturn(List.of(coupon1, coupon2));

			// when & then
			mockMvc.perform(get("/api/coupons/{userId}", testUserId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.userId").value(testUserId))
				.andExpect(jsonPath("$.data.couponResponseList").isArray())
				.andExpect(jsonPath("$.data.couponResponseList.length()").value(2))
				.andExpect(jsonPath("$.data.couponResponseList[*].couponName", hasItem("1000원 할인 쿠폰")))
				.andExpect(jsonPath("$.data.couponResponseList[*].couponName", hasItem("2000원 할인 쿠폰")));
		}
	}
}