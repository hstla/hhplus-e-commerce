package kr.hhplus.be.server.config.jpa.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.product.repository.ProductOptionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {

	@Mock
	private ProductOptionRepository productOptionRepository;

	@InjectMocks
	private ProductService productService;

	@Nested
	@DisplayName("orderProductOptions 메서드는")
	class Describe_orderProductOptions {

		@Test
		@DisplayName("유효한 요청 리스트가 주어지면 재고 차감 후 ProductOption 리스트를 반환한다")
		void orderProductOptions_success() {
			// given
			ProductInput.order order1 = ProductInput.order.of(1L, 2);
			ProductInput.order order2 = ProductInput.order.of(2L, 3);

			ProductOption option1 = ProductOption.create(10L, "옵션1", 1000L, 5);
			ProductOption option2 = ProductOption.create(20L, "옵션2", 2000L, 10);

			given(productOptionRepository.findById(1L)).willReturn(option1);
			given(productOptionRepository.findById(2L)).willReturn(option2);

			// when
			List<ProductOption> result = productService.orderProductOptions(List.of(order1, order2));

			// then
			then(productOptionRepository).should(times(1)).findById(1L);
			then(productOptionRepository).should(times(1)).findById(2L);

			assertThat(result).containsExactly(option1, option2);
			assertThat(option1.getStock()).isEqualTo(3); // 5 - 2
			assertThat(option2.getStock()).isEqualTo(7); // 10 - 3
		}

		@Test
		@DisplayName("존재하지 않는 옵션ID로 조회 시 예외를 던진다")
		void orderProductOptions_optionNotFound() {
			// given
			ProductInput.order order = ProductInput.order.of(999L, 1);
			given(productOptionRepository.findById(999L)).willThrow(new RestApiException(ProductErrorCode.NOT_FOUND_PRODUCT_OPTION));

			// when & then
			assertThatThrownBy(() -> productService.orderProductOptions(List.of(order)))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT_OPTION.getMessage());
		}

		@Test
		@DisplayName("재고 부족 시 예외를 던진다")
		void orderProductOptions_outOfStock() {
			// given
			ProductInput.order order = ProductInput.order.of(1L, 10);
			ProductOption option = ProductOption.create(10L, "옵션", 1000L, 5);
			given(productOptionRepository.findById(1L)).willReturn(option);

			// when & then
			assertThatThrownBy(() -> productService.orderProductOptions(List.of(order)))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.OUT_OF_STOCK.getMessage());
		}
	}
}