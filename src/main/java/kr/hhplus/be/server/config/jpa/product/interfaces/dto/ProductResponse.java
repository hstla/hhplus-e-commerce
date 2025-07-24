package kr.hhplus.be.server.config.jpa.product.interfaces.dto;

import java.util.List;

import kr.hhplus.be.server.config.jpa.product.application.ProductResult;
import kr.hhplus.be.server.config.jpa.product.domain.ProductCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {

	@Getter
	@NoArgsConstructor
	public static class ProductOptions {
		private Long productId;
		private String productName;
		private String productDescription;
		private ProductCategory productCategory;
		private List<ProductOption> productOptions;

		private ProductOptions(Long productId, String productName, String productDescription,
			ProductCategory productCategory,
			List<ProductOption> productOptions) {
			this.productId = productId;
			this.productName = productName;
			this.productDescription = productDescription;
			this.productCategory = productCategory;
			this.productOptions = productOptions;
		}

		public static ProductOptions of(Long productId, String productName, String productDescription,
			ProductCategory productCategory,
			List<ProductResult.Option> optionResults) {
			List<ProductOption> options = optionResults.stream().map(
				or -> ProductOption.of(or.getProductOptionId(), or.getOptionName(), or.getPrice(), or.getStock())
			).toList();

			return new ProductOptions(productId, productName, productDescription, productCategory, options);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class ProductRankList {
		private Long productOptionId;
	}


	@Getter
	@NoArgsConstructor
	public static class ProductOption {
		private Long productOptionId;
		private String optionName;
		private Long price;
		private int stock;

		private ProductOption(Long productOptionId, String optionName, Long price, int stock) {
			this.productOptionId = productOptionId;
			this.optionName = optionName;
			this.price = price;
			this.stock = stock;
		}

		public static ProductOption of(Long productOptionId, String optionName, Long price, int stock) {
			return new ProductOption(productOptionId, optionName, price, stock);
		}
	}
}