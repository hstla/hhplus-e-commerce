package kr.hhplus.be.server.config.jpa.product.domain;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

	@Getter
	@NoArgsConstructor
	public static class ProductOptionInfo {
		private Long productId;
		private String productName;
		private ProductCategory category;
		private String description;
		private List<OptionInfo> options;

		private ProductOptionInfo(Long productId, String productName, ProductCategory category, String description,
			List<OptionInfo> options) {
			this.productId = productId;
			this.productName = productName;
			this.category = category;
			this.description = description;
			this.options = options;
		}

		public static ProductOptionInfo of(Long productId, String productName, ProductCategory category, String description, List<ProductOption> productOptions) {
			List<ProductInfo.OptionInfo> options = productOptions.stream().map(ProductInfo.OptionInfo::of).toList();
			return new ProductOptionInfo(productId, productName, category, description, options);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class OptionInfo {
		private Long productOptionId;
		private String optionName;
		private Long price;
		private int stock;

		private OptionInfo(Long productOptionId, String optionName, Long price, int stock) {
			this.productOptionId = productOptionId;
			this.optionName = optionName;
			this.price = price;
			this.stock = stock;
		}

		public static OptionInfo of(ProductOption productOption) {
			return new OptionInfo(productOption.getId(), productOption.getOptionName(), productOption.getPrice(), productOption.getStock());
		}
	}
}