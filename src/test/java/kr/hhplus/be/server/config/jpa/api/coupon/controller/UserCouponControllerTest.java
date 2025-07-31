package kr.hhplus.be.server.config.jpa.api.coupon.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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

import kr.hhplus.be.server.config.jpa.api.coupon.controller.dto.UserCouponRequest;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.coupon.JpaCouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.usercoupon.JpaUserCouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.config.jpa.user.domain.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserCouponController 통합 테스트")
class UserCouponControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserCouponRepository userCouponRepository;
	@Autowired
	private JpaUserCouponRepository jpaUserCouponRepository;
	@Autowired
	private JpaCouponRepository jpaCouponRepository;
	@Autowired
	private CouponRepository couponRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JpaUserRepository jpaUserRepository;

	private Long testUserId;
	private Long testCouponId1;
	private Long testCouponId2;

	@BeforeEach
	void setUp() {
		jpaUserCouponRepository.deleteAll();
		jpaCouponRepository.deleteAll();
		jpaUserRepository.deleteAll();

		User newUser = userRepository.save(User.signUpUser("newUser", "test@email.com", "12345"));
		testUserId = newUser.getId();
		Coupon coupon1 = Coupon.create(CouponType.FIXED, "1000원 할인 쿠폰", 1_000L, 10, LocalDateTime.now().plusDays(10));
		Coupon coupon2 = Coupon.create(CouponType.FIXED, "2000원 할인 쿠폰", 2_000L, 10, LocalDateTime.now().plusDays(10));
		testCouponId1 = couponRepository.save(coupon1).getId();
		testCouponId2 = couponRepository.save(coupon2).getId();
		UserCoupon userCoupon2 = UserCoupon.publish(testUserId, testCouponId1, LocalDateTime.now());
		userCouponRepository.save(userCoupon2);
	}

	@Nested
	@DisplayName("POST /api/coupons/publish - 쿠폰 발행")
	class PublishCoupon {
		@Test
		@DisplayName("쿠폰 발행 요청에 성공하고 발행된 쿠폰 정보를 반환한다")
		void publishCoupon_success() throws Exception {
			UserCouponRequest.Publish request = UserCouponRequest.Publish.of(testUserId, testCouponId2);

			mockMvc.perform(post("/api/coupons/publish")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.couponName").value("2000원 할인 쿠폰"))
				.andExpect(jsonPath("$.data.discountValue").value(2_000L));
		}
	}

	@Nested
	@DisplayName("GET /api/coupons/{userId} - 사용자 쿠폰 조회")
	class GetUserCoupon {
		@Test
		@DisplayName("존재하는 사용자 쿠폰 리스트를 반환한다")
		void getUserCoupon_success() throws Exception {
			mockMvc.perform(get("/api/coupons/{userId}", testUserId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.userId").value(testUserId))
				.andExpect(jsonPath("$.data.couponResponseList").isArray())
				.andExpect(jsonPath("$.data.couponResponseList[0].userCouponId").value(testCouponId1))
				.andExpect(jsonPath("$.data.couponResponseList[0].couponName").value("1000원 할인 쿠폰"));
		}
	}
}