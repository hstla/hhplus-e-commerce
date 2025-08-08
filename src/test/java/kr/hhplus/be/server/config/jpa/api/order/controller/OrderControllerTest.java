// package kr.hhplus.be.server.config.jpa.api.order.controller;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.List;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import kr.hhplus.be.server.config.jpa.api.order.controller.dto.OrderRequest;
// import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
// import kr.hhplus.be.server.config.jpa.product.infrastructure.JpaProductOptionRepository;
// import kr.hhplus.be.server.config.jpa.user.infrastructure.JpaUserRepository;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @DisplayName("OrderController 통합 테스트")
// class OrderControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
// 	@Autowired
// 	private ObjectMapper objectMapper;
// 	@Autowired
// 	private JpaUserRepository jpaUserRepository;
// 	@Autowired
// 	private JpaProductOptionRepository jpaProductOptionRepository;
//
// 	private Long userId;
// 	private Long optionId1;
// 	private Long optionId2;
//
// 	@BeforeEach
// 	void setUp() {
// 		jpaUserRepository.deleteAll();
// 		jpaProductOptionRepository.deleteAll();
//
// 		userId = jpaUserRepository.save(new UserEntity("name", "test@email.com", "12345", 0L)).getId();
// 		optionId1 = jpaProductOptionRepository.save(new ProductOptionEntity(1L, "option1Name", 1_000L, 10)).getId();
// 		optionId2 = jpaProductOptionRepository.save(new ProductOptionEntity(1L, "option1Name", 2_000L, 10)).getId();
// 	}
//
// 	@Nested
// 	@DisplayName("order create")
// 	class CreateOrder {
//
// 		@Test
// 		@DisplayName("주문 생성 성공한다")
// 		void create_order_success() throws Exception {
// 			// given
// 			OrderRequest.Order request = OrderRequest.Order.of(
// 				userId,
// 				null,
// 				List.of(
// 					OrderRequest.OrderProduct.of(optionId1, 2),
// 					OrderRequest.OrderProduct.of(optionId2, 1)
// 				)
// 			);
//
// 			// when then
// 			mockMvc.perform(post("/api/orders")
// 					.contentType(MediaType.APPLICATION_JSON)
// 					.content(objectMapper.writeValueAsString(request)))
// 				.andExpect(status().isOk())
// 				.andExpect(jsonPath("$.data.totalPrice").value(4_000L))
// 				.andExpect(jsonPath("$.data.status").value(OrderStatus.CREATED.name()))
// 				.andReturn()
// 				.getResponse();
// 		}
// 	}
// }