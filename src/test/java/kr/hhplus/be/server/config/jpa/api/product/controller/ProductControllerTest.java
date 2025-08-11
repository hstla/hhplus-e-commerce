package kr.hhplus.be.server.config.jpa.api.product.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.hhplus.be.server.config.jpa.api.product.usecase.FindProductUseCase;
import kr.hhplus.be.server.config.jpa.api.product.usecase.dto.ProductResult;
import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController 단위 테스트")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindProductUseCase findProductUseCase;

    @Nested
    @DisplayName("GET /api/products/{productId} - 상품 상세 조회")
    class GetProductDetails {

        @Test
        @DisplayName("상품 상세 정보를 정상적으로 반환한다")
        void getProductDetails_success() throws Exception {
            // given
            Long productId = 1L;
            List<ProductResult.Option> options = Collections.singletonList(
                new ProductResult.Option(1L, "Option 1", 1000L, 10)
            );
            ProductResult.ProductOptionInfo productInfo = new ProductResult.ProductOptionInfo(
                productId, "Test Product", ProductCategory.FOOD, "Test Description", options
            );

            given(findProductUseCase.findProductOptionsById(productId)).willReturn(productInfo);

            // when // then
            mockMvc.perform(get("/api/products/{productId}", productId)
                    .accept(MediaType.APPLICATION_JSON))
					.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productId").value(productId))
                    .andExpect(jsonPath("$.data.productName").value("Test Product"))
                    .andExpect(jsonPath("$.data.productOptions[*].optionName", hasItem("Option 1")));
        }

        @Test
        @DisplayName("유효하지 않은 상품 ID로 조회 시 실패한다")
        void getProductDetails_fail_invalidId() throws Exception {
			// given
			long nonProductId = -1L;

            // when // then
            mockMvc.perform(get("/api/products/{productId}", nonProductId)
                    .accept(MediaType.APPLICATION_JSON))
					.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}