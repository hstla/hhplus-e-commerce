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
public class ProductOption {
	private Long id;
	private Long productId;
	private String optionName;
	private Long price;
	private int stock;

	public static ProductOption create(Long productId, String optionName, Long price, int stockQuantity) {
		validateStock(stockQuantity);
		validateName(optionName);
		validatePrice(price);
		return new ProductOption(null, productId, optionName, price, stockQuantity);
	}

	private static void validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new RestApiException(ProductErrorCode.INVALID_OPTION_NAME);
		}
	}

	private static void validateStock(int stock) {
		if (stock < 0) {
			throw new RestApiException(ProductErrorCode.INVALID_STOCK);
		}
	}

	private static void validatePrice(Long price) {
		if (price == null || price < 0) {
			throw new RestApiException(ProductErrorCode.INVALID_PRICE);
		}
	}

	public void order(int quantity) {
		validateOrderStock(quantity);
		this.stock -= quantity;
	}

	private void validateOrderStock(int quantity) {
		if (this.stock < quantity) {
			throw new RestApiException(ProductErrorCode.OUT_OF_STOCK);
		}
	}
}