package kr.hhplus.be.server.config.jpa.api.usercoupon.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.config.jpa.api.usercoupon.controller.dto.UserCouponRequest;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.FindUserCouponUseCase;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.PublishCouponUseCase;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.JpaCouponRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.infrastructure.JpaUserCouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.model.User;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;

@WebMvcTest(UserCouponController.class)
@DisplayName("UserCouponController 단위 테스트")
class UserCouponControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Mock
	private PublishCouponUseCase publishUserCouponUseCase;
	@Mock
	private FindUserCouponUseCase findUserCouponUseCase;

	private final Long testUserId = 1L;
	private final Long testCouponId1 = 101L;
	private final Long testCouponId2 = 102L;

	@Nested
	@DisplayName("POST /api/coupons/publish - 쿠폰 발행")
	class PublishCoupon {

		@Test
		@DisplayName("쿠폰 발행 성공")
		void publishCoupon_success() throws Exception {
			// given
			UserCouponRequest.Publish request = UserCouponRequest.Publish.of(testUserId, testCouponId2);
			CouponResult.UserCouponInfo response = new CouponResult.UserCouponInfo(1L, testUserId, testCouponId2, "2000원 할인 쿠폰", 2000L);

			given(publishUserCouponUseCase.execute(any())).willReturn(response);

			// when & then
			mockMvc.perform(post("/api/coupons/publish")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.couponName").value("2000원 할인 쿠폰"))
				.andExpect(jsonPath("$.data.discountValue").value(2_000));
		}
	}

	@Nested
	@DisplayName("GET /api/coupons/{userId} - 사용자 쿠폰 조회")
	class GetUserCoupon {

		@Test
		@DisplayName("쿠폰 조회 성공")
		void getUserCoupon_success() throws Exception {
			// given
			CouponResult.UserCouponInfo coupon1 = new CouponResult.UserCouponInfo(11L, testUserId, testCouponId1, "1000원 할인 쿠폰", 1000L);
			CouponResult.UserCouponInfo coupon2 = new CouponResult.UserCouponInfo(12L, testUserId, testCouponId2, "2000원 할인 쿠폰", 2000L);

			given(findUserCouponUseCase.execute(testUserId)).willReturn(List.of(coupon1, coupon2));

			// when & then
			mockMvc.perform(get("/api/coupons/{userId}", testUserId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.userId").value(testUserId))
				.andExpect(jsonPath("$.data.couponResponseList").isArray())
				.andExpect(jsonPath("$.data.couponResponseList.length()").value(2))
				.andExpect(jsonPath("$.data.couponResponseList[0].couponName").value("1000원 할인 쿠폰"))
				.andExpect(jsonPath("$.data.couponResponseList[1].couponName").value("2000원 할인 쿠폰"));
		}
	}
}