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

		@Test
		@DisplayName("name이 null이면 INVALID_PRODUCT_NAME 예외를 던진다")
		void throw_when_name_is_null() {
			// given
			String name = null;
			ProductCategory category = ProductCategory.CLOTHING;
			String description = "Nice";

			// when & then
			assertThatThrownBy(() -> Product.create(name, category, description))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.INVALID_PRODUCT_NAME.getMessage());
		}

		@Test
		@DisplayName("name이 공백이면 INVALID_PRODUCT_NAME 예외를 던진다")
		void throw_when_name_is_blank() {
			// given
			String name = "   ";
			ProductCategory category = ProductCategory.CLOTHING;
			String description = "Nice";

			// when & then
			assertThatThrownBy(() -> Product.create(name, category, description))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.INVALID_PRODUCT_NAME.getMessage());
		}

		@Test
		@DisplayName("description이 null이면 INVALID_DESCRIPTION 예외를 던진다")
		void throw_when_description_is_null() {
			// given
			String name = "Keyboard";
			ProductCategory category = ProductCategory.CLOTHING;
			String description = null;

			// when & then
			assertThatThrownBy(() -> Product.create(name, category, description))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.INVALID_DESCRIPTION.getMessage());
		}

		@Test
		@DisplayName("description이 공백이면 INVALID_DESCRIPTION 예외를 던진다")
		void throw_when_description_is_blank() {
			// given
			String name = "Keyboard";
			ProductCategory category = ProductCategory.CLOTHING;
			String description = "   ";

			// when & then
			assertThatThrownBy(() -> Product.create(name, category, description))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.INVALID_DESCRIPTION.getMessage());
		}
	}
}