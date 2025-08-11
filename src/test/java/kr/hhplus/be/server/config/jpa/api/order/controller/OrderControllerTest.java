package kr.hhplus.be.server.config.jpa.api.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
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

import kr.hhplus.be.server.config.jpa.api.order.controller.dto.OrderRequest;
import kr.hhplus.be.server.config.jpa.api.order.usecase.CreateOrderUseCase;
import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController 단위 테스트")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @Nested
    @DisplayName("POST /api/orders - 주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("주문 생성에 성공한다")
        void createOrder_success() throws Exception {
            // given
            OrderRequest.Order request = new OrderRequest.Order(1L, null, 
                List.of(new OrderRequest.OrderProduct(1L, 1)));
            OrderResult.Order result = new OrderResult.Order(1L, 1L, null, 10_000L, OrderStatus.CREATED);
            given(createOrderUseCase.execute(any())).willReturn(result);

            // when & then
            mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.totalPrice").value(10_000L));
        }

        @ParameterizedTest
        @MethodSource("invalidOrderRequests")
        @DisplayName("유효하지 않은 요청값으로 주문 생성에 실패한다")
        void createOrder_fail_invalidRequest(OrderRequest.Order request) throws Exception {
            // when & then
            mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        static Stream<Arguments> invalidOrderRequests() {
            return Stream.of(
                Arguments.of(new OrderRequest.Order(null, null, List.of(new OrderRequest.OrderProduct(1L, 1)))),
                Arguments.of(new OrderRequest.Order(-1L, null, List.of(new OrderRequest.OrderProduct(1L, 1)))),
                Arguments.of(new OrderRequest.Order(1L, -1L, List.of(new OrderRequest.OrderProduct(1L, 1)))),
                Arguments.of(new OrderRequest.Order(1L, null, Collections.emptyList())),
                Arguments.of(new OrderRequest.Order(1L, null, List.of(new OrderRequest.OrderProduct(null, 1)))),
                Arguments.of(new OrderRequest.Order(1L, null, List.of(new OrderRequest.OrderProduct(-1L, 1)))),
                Arguments.of(new OrderRequest.Order(1L, null, List.of(new OrderRequest.OrderProduct(1L, 0))))
            );
        }

        @Test
        @DisplayName("주문 처리 중 오류가 발생하여 실패한다")
        void createOrder_fail_exception() throws Exception {
            // given
            OrderRequest.Order request = new OrderRequest.Order(1L, null, 
                List.of(new OrderRequest.OrderProduct(1L, 1)));
            given(createOrderUseCase.execute(any())).willThrow(new RestApiException(UserErrorCode.INACTIVE_USER));

            // when & then
            mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }
}
