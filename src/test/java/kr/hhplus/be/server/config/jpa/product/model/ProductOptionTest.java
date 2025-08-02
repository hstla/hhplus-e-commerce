package kr.hhplus.be.server.config.jpa.product.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;

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
			assertThat(result.getOptionName()).isEqualTo(optionName);
			assertThat(result.getPrice()).isEqualTo(price);
			assertThat(result.getStock()).isEqualTo(stock);
		}

		@Test
		@DisplayName("옵션명이 null 또는 빈 문자열이면 예외가 발생한다")
		void create_fail_invalid_option_name() {
			// given
			String invalidName = " ";

			// when & then
			assertThatThrownBy(() ->
				ProductOption.create(1L, invalidName, 1000L, 10)
			).isInstanceOf(RestApiException.class)
				.hasMessageContaining(ProductErrorCode.INVALID_OPTION_NAME.getMessage());
		}

		@Test
		@DisplayName("재고가 0보다 작으면 예외가 발생한다")
		void create_fail_invalid_stock() {
			// given
			int invalidStock = -1;

			// when & then
			assertThatThrownBy(() ->
				ProductOption.create(1L, "옵션A", 1000L, invalidStock)
			).isInstanceOf(RestApiException.class)
				.hasMessageContaining(ProductErrorCode.INVALID_STOCK.getMessage());
		}

		@Test
		@DisplayName("가격이 null 이거나 0보다 작으면 예외가 발생한다")
		void create_fail_invalid_price() {
			// when & then
			assertThatThrownBy(() ->
				ProductOption.create(1L, "옵션A", null, 10)
			).isInstanceOf(RestApiException.class)
				.hasMessageContaining(ProductErrorCode.INVALID_PRICE.getMessage());

			assertThatThrownBy(() ->
				ProductOption.create(1L, "옵션A", -1L, 10)
			).isInstanceOf(RestApiException.class)
				.hasMessageContaining(ProductErrorCode.INVALID_PRICE.getMessage());
		}
	}

	@Nested
	@DisplayName("order 메서드는")
	class OrderTest {

		@Test
		@DisplayName("재고가 충분하면 주문 수량만큼 차감된다")
		void order_success() {
			// given
			ProductOption option = ProductOption.create(1L, "옵션", 1000L, 10);
			int orderQty = 3;

			// when
			option.order(orderQty);

			// then
			assertThat(option.getStock()).isEqualTo(7);
		}

		@Test
		@DisplayName("재고가 부족하면 예외가 발생한다")
		void order_fail_out_of_stock() {
			// given
			ProductOption option = ProductOption.create(1L, "옵션", 1000L, 2);
			int orderQty = 3;

			// when & then
			assertThatThrownBy(() ->
				option.order(orderQty)
			).isInstanceOf(RestApiException.class)
				.hasMessageContaining(ProductErrorCode.OUT_OF_STOCK.getMessage());
		}
	}
}