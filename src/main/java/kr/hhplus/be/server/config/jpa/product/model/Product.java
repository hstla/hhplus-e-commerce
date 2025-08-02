package kr.hhplus.be.server.config.jpa.product.model;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product {
	private Long id;
	private String name;
	private ProductCategory category;
	private String description;

	public static Product create(String name, ProductCategory category, String description) {
		validateName(name);
		validateDescription(description);
		return new Product(null, name, category, description);
	}

	private static void validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new RestApiException(ProductErrorCode.INVALID_PRODUCT_NAME);
		}
	}

	private static void validateDescription(String description) {
		if (description == null || description.isBlank()) {
			throw new RestApiException(ProductErrorCode.INVALID_DESCRIPTION);
		}
	}
}