package kr.hhplus.be.server.config.jpa.api.payment.controller;

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

import kr.hhplus.be.server.config.jpa.api.payment.controller.dto.PaymentRequest;
import kr.hhplus.be.server.config.jpa.order.infrastructure.order.JpaOrderRepository;
import kr.hhplus.be.server.config.jpa.order.infrastructure.order.OrderEntity;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentType;
import kr.hhplus.be.server.config.jpa.user.domain.infrastructure.JpaUserRepository;
import kr.hhplus.be.server.config.jpa.user.domain.infrastructure.UserEntity;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("PaymentController 통합 테스트")
class PaymentControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	JpaOrderRepository jpaOrderRepository;
	@Autowired
	JpaUserRepository jpaUserRepository;

	private Long orderId;
	private Long userId;

	@BeforeEach
	void setUp() {
		jpaOrderRepository.deleteAll();
		jpaUserRepository.deleteAll();

		orderId = jpaOrderRepository.save(new OrderEntity(1L, null, 10_000L, OrderStatus.CREATED, LocalDateTime.now().minusMinutes(2))).getId();
		userId = jpaUserRepository.save(new UserEntity("name", "test@email.com", "12345", 20_000L)).getId();
	}

	@Nested
	@DisplayName("결제 생성 API (/api/payments)")
	class CreatePaymentTest {

		@Test
		@DisplayName("정상적으로 결제를 생성한다")
		void shouldCreatePaymentSuccessfully() throws Exception {
			// given
			PaymentRequest.Payment request = PaymentRequest.Payment.of(orderId, userId, PaymentType.POINT);

			// when & then
			mockMvc.perform(post("/api/payments")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.orderId").value(orderId))
				.andExpect(jsonPath("$.data.paymentAmount").value(10_000L))
				.andExpect(jsonPath("$.data.status").value(PaymentStatus.COMPLETED.name()));
		}
	}
}