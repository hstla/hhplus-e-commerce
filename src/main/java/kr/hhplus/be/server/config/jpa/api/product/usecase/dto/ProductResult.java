package kr.hhplus.be.server.config.jpa.api.product.usecase.dto;

import java.util.List;

import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResult {

	@Getter
	@NoArgsConstructor
	public static class ProductOptionInfo {
		private Long productId;
		private String productName;
		private ProductCategory category;
		private String description;
		private List<Option> options;

		private ProductOptionInfo(Long productId, String productName, ProductCategory category, String description,
			List<Option> options) {
			this.productId = productId;
			this.productName = productName;
			this.category = category;
			this.description = description;
			this.options = options;
		}

		public static ProductOptionInfo of(Product product, List<ProductOption> productOption) {
			List<Option> options = productOption.stream().map(Option::of).toList();
			return new ProductOptionInfo(product.getId(), product.getName(), product.getCategory(), product.getDescription(), options);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class Option {
		private Long productOptionId;
		private String optionName;
		private Long price;
		private int stock;

		private Option(Long productOptionId, String optionName, Long price, int stock) {
			this.productOptionId = productOptionId;
			this.optionName = optionName;
			this.price = price;
			this.stock = stock;
		}

		public static Option of(ProductOption productOption) {
			return new Option(productOption.getId(),  productOption.getOptionName(), productOption.getPrice(), productOption.getStock());
		}
	}
}