package kr.hhplus.be.api.product.controller.dto;

import java.util.List;

import kr.hhplus.be.api.product.usecase.dto.ProductResult;
import kr.hhplus.be.domain.product.model.ProductCategory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {

	public record ProductOptions(
		Long productId,
		String productName,
		String productDescription,
		ProductCategory productCategory,
		List<ProductOption> productOptions
	) {
		public static ProductOptions of(
			Long productId,
			String productName,
			String productDescription,
			ProductCategory productCategory,
			List<ProductResult.Option> optionResults
		) {
			List<ProductOption> options = optionResults.stream()
				.map(or -> new ProductOption(
					or.productOptionId(),
					or.optionName(),
					or.price(),
					or.stock()))
				.toList();

			return new ProductOptions(productId, productName, productDescription, productCategory, options);
		}
	}

	public record ProductOption(
		Long productOptionId,
		String optionName,
		Long price,
		int stock
	) {
	}
}