package kr.hhplus.be.server.config.jpa.api.product.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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

import kr.hhplus.be.server.config.jpa.product.infrastructure.product.JpaProductRepository;
import kr.hhplus.be.server.config.jpa.product.infrastructure.product.ProductEntity;
import kr.hhplus.be.server.config.jpa.product.infrastructure.productoption.JpaProductOptionRepository;
import kr.hhplus.be.server.config.jpa.product.infrastructure.productoption.ProductOptionEntity;
import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ProductController 통합 테스트")
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JpaProductRepository jpaProductRepository;
	@Autowired
	private JpaProductOptionRepository jpaProductOptionRepository;

	private Long testProductId;

	@BeforeEach
	void setUp() {
		jpaProductOptionRepository.deleteAll();
		jpaProductRepository.deleteAll();

		// 상품 생성
		testProductId = jpaProductRepository.save(new ProductEntity("테스트 상품", ProductCategory.FOOD, "테스트 설명")).getId();

		// 옵션 추가
		List<ProductOptionEntity> options = List.of(
			new ProductOptionEntity(testProductId, "레귤러 사이즈", 3000L, 10),
			new ProductOptionEntity(testProductId, "라지 사이즈", 4000L, 10)
		);
		jpaProductOptionRepository.saveAll(options);
	}

	@Nested
	@DisplayName("GET /api/products/{productId} - 상품 상세 조회")
	class GetProductDetails {

		@Test
		@DisplayName("상품 상세 정보를 정상적으로 반환한다")
		void getProductDetails_success() throws Exception {
			mockMvc.perform(get("/api/products/{productId}", testProductId)
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.productId").value(testProductId))
				.andExpect(jsonPath("$.data.productName").value("테스트 상품"))
				.andExpect(jsonPath("$.data.productDescription").value("테스트 설명"))
				.andExpect(jsonPath("$.data.productCategory").value(ProductCategory.FOOD.name()))
				.andExpect(jsonPath("$.data.productOptions").isArray())
				.andExpect(jsonPath("$.data.productOptions.length()").value(2))
				.andExpect(jsonPath("$.data.productOptions[*].optionName", hasItem("레귤러 사이즈")))
				.andExpect(jsonPath("$.data.productOptions[*].price", hasItem(3000)));
		}
	}
}