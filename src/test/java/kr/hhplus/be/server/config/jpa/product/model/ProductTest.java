package kr.hhplus.be.server.config.jpa.product.model;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;

@ExtendWith(MockitoExtension.class)
@DisplayName("procut 도메인 단위 테스트")
class ProductTest {

	@Nested
	@DisplayName("createProduct 테스트는")
	class createProductTest {

		@Test
		@DisplayName("유효한 값이 주어지면 Product를 생성한다")
		void create_valid_product() {
			// given
			String name = "Keyboard";
			ProductCategory category = ProductCategory.CLOTHING;
			String description = "Mechanical keyboard";

			// when
			Product product = Product.create(name, category, description);

			// then
			assertThat(product).isNotNull();
			assertThat(product.getName()).isEqualTo(name);
			assertThat(product.getCategory()).isEqualTo(category);
			assertThat(product.getDescription()).isEqualTo(description);
		}

		@ParameterizedTest
		@MethodSource("invalidNames")
		@DisplayName("name의 길이가 허용 범위를 초과하면 예외가 발생한다")
		void throw_when_name_fail(String name) {
			// given
			ProductCategory category = ProductCategory.CLOTHING;
			String description = "Mechanical keyboard";

			// when & then
			assertThatThrownBy(() -> Product.create(name, category, description))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.INVALID_PRODUCT_NAME.getMessage());
		}

		static Stream<String> invalidNames() {
			return Stream.of(
				"",
				"a".repeat(31)
			);
		}

		@ParameterizedTest
		@MethodSource("invalidDescription")
		@DisplayName("description길이가 길이가 허용 범위를 초과하면 예외가 발생한다")
		void throw_when_description_fail(String description) {
			// given
			String name = "Keyboard";
			ProductCategory category = ProductCategory.CLOTHING;

			// when & then
			assertThatThrownBy(() -> Product.create(name, category, description))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.INVALID_DESCRIPTION.getMessage());
		}

		static Stream<String> invalidDescription() {
			return Stream.of(
				"",
				"a".repeat(201)
			);
		}
	}
}