package kr.hhplus.be.server.config.jpa.api.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.config.jpa.api.payment.controller.dto.PaymentRequest;
import kr.hhplus.be.server.config.jpa.api.payment.usecase.CreatePaymentUseCase;
import kr.hhplus.be.server.config.jpa.api.payment.usecase.PaymentResult;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentType;

@WebMvcTest(PaymentController.class)
@DisplayName("PaymentController 단위 테스트")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreatePaymentUseCase createPaymentUseCase;

    @Nested
    @DisplayName("POST /api/payments - 결제 생성")
    class CreatePayment {

		@Test
		@DisplayName("결제 생성에 성공한다")
		void createPayment_success() throws Exception {
			// given
			PaymentRequest.Payment request = new PaymentRequest.Payment(1L, 1L, PaymentType.POINT);
			PaymentResult.Pay result = new PaymentResult.Pay(1L, 1L, 10000L, PaymentStatus.COMPLETED);
			given(createPaymentUseCase.execute(any())).willReturn(result);

			// when & then
			mockMvc.perform(post("/api/payments")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.orderId").value(1L))
				.andExpect(jsonPath("$.data.paymentAmount").value(10000L));
		}

		@ParameterizedTest
		@CsvSource({
			"-1, 1, POINT",
			"1, -1, POINT"
		})
		@DisplayName("유효하지 않은 요청값으로 결제 생성에 실패한다")
		void createPayment_fail_invalidRequest(Long orderId, Long userId, PaymentType type) throws Exception {
			// given
			PaymentRequest.Payment request = new PaymentRequest.Payment(orderId, userId, type);

			// when & then
			mockMvc.perform(post("/api/payments")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}
	}
}