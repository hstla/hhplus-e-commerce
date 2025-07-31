package kr.hhplus.be.server.config.jpa.product.application;

import java.util.List;

import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResult {

	@Getter
	@NoArgsConstructor
	public static class ProductOption {
		private Long productId;
		private String productName;
		private ProductCategory category;
		private String description;
		private List<Option>  options;

		private ProductOption(Long productId, String productName, ProductCategory category, String description,
			List<Option> options) {
			this.productId = productId;
			this.productName = productName;
			this.category = category;
			this.description = description;
			this.options = options;
		}

		public static ProductOption of(Long productId, String productName, ProductCategory category, String description, List<ProductInfo.OptionInfo> optionInfoList) {
			List<ProductResult.Option> options = optionInfoList.stream().map(
				os -> ProductResult.Option.of(os.getProductOptionId(), os.getOptionName(), os.getPrice(), os.getStock())
			).toList();
			return new ProductOption(productId, productName, category, description, options);
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

		public static Option of(Long productOptionId, String optionName, Long price, int stock) {
			return new Option(productOptionId, optionName, price, stock);
		}
	}
}