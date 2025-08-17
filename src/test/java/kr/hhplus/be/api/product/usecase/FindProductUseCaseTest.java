package kr.hhplus.be.api.product.usecase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.api.product.usecase.dto.ProductResult;
import kr.hhplus.be.domain.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.global.error.ProductErrorCode;
import kr.hhplus.be.global.error.RestApiException;

@DisplayName("FindProductUseCase 통합 테스트")
class FindProductUseCaseTest extends IntegrationTestConfig {

    @Autowired
    private JpaProductRepository jpaProductRepository;
    @Autowired
    private JpaProductOptionRepository jpaProductOptionRepository;
    @Autowired
    private FindProductUseCase findProductUseCase;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        jpaProductOptionRepository.deleteAll();
        jpaProductRepository.deleteAll();

        Product product = Product.create("Test Product", ProductCategory.FOOD, "Description");
        savedProduct = jpaProductRepository.save(product);

        ProductOption option1 = ProductOption.create(savedProduct.getId(), "Option 1", 1_000L, 10);
        ProductOption option2 = ProductOption.create(savedProduct.getId(), "Option 2", 1_500L, 5);
        jpaProductOptionRepository.saveAll(List.of(option1, option2));
    }

    @Nested
    @DisplayName("상품 및 옵션 조회")
    class FindProductWithOptions {

        @Test
        @DisplayName("상품 ID로 상품 및 옵션 조회에 성공한다")
        void findProductWithOptions_success() {
            // when
            ProductResult.ProductOptionInfo result = findProductUseCase.findProductOptionsById(savedProduct.getId());

            // then
            assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(savedProduct.getId(), result.productId()),
                () -> assertEquals("Test Product", result.productName()),
                () -> assertEquals(2, result.options().size())
            );
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회 시 실패한다")
        void findProductWithOptions_fail_productNotFound() {
            // given
            Long notExistProductId = 999L;

            // when & then
			Assertions.assertThatThrownBy(() -> findProductUseCase.findProductOptionsById(notExistProductId))
					.isInstanceOf(RestApiException.class)
					.hasMessage(ProductErrorCode.INACTIVE_PRODUCT.getMessage());
        }
    }
}