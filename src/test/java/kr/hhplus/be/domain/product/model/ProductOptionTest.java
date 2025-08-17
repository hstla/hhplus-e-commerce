package kr.hhplus.be.domain.product.model;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.global.error.ProductErrorCode;
import kr.hhplus.be.global.error.RestApiException;

@ExtendWith(MockitoExtension.class)
@DisplayName("procutOption 도메인 단위 테스트")
class ProductOptionTest {

	@Nested
	@DisplayName("create 메서드는")
	class CreateTest {

		@Test
		@DisplayName("유효한 값이 들어오면 ProductOption을 생성한다")
		void create_success() {
			// given
			Long productId = 1L;
			String optionName = "옵션A";
			Long price = 1000L;
			int stock = 10;

			// when
			ProductOption result = ProductOption.create(productId, optionName, price, stock);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getProductId()).isEqualTo(productId);
			assertThat(result.getName()).isEqualTo(optionName);
			assertThat(result.getPrice()).isEqualTo(price);
			assertThat(result.getStock()).isEqualTo(stock);
		}

		@ParameterizedTest
		@MethodSource("invalidNames")
		@DisplayName("옵션명이 길이가 허용 범위를 초과하면 예외가 발생한다")
		void create_fail_invalid_option_name(String name) {
			// given
			// when & then
			assertThatThrownBy(() ->
				ProductOption.create(1L, name, 1000L, 10)
			).isInstanceOf(RestApiException.class)
				.hasMessageContaining(ProductErrorCode.INVALID_OPTION_NAME.getMessage());
		}

		private static Stream<String> invalidNames() {
			return Stream.of(
				"",
				"a".repeat(31)
			);
		}
	}

	@Nested
	@DisplayName("orderDecreaseStock 메서드는")
	class orderDecreaseStockTest {

		@Test
		@DisplayName("재고가 충분하면 주문 수량만큼 차감된다")
		void order_success() {
			// given
			ProductOption option = ProductOption.create(1L, "옵션", 1000L, 10);
			int orderQty = 10;

			// when
			option.orderDecreaseStock(orderQty);

			// then
			assertThat(option.getStock()).isEqualTo(0);
		}

		@Test
		@DisplayName("재고가 부족하면 예외가 발생한다")
		void order_fail_out_of_stock() {
			// given
			ProductOption option = ProductOption.create(1L, "옵션", 1000L, 2);
			int orderQty = 3;

			// when & then
			assertThatThrownBy(() ->
				option.orderDecreaseStock(orderQty)
			).isInstanceOf(RestApiException.class)
				.hasMessageContaining(ProductErrorCode.OUT_OF_STOCK.getMessage());
		}
	}
}