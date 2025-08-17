package kr.hhplus.be.api.product.usecase.dto;

import java.util.List;

import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResult {

	public record ProductOptionInfo(
		Long productId,
		String productName,
		ProductCategory category,
		String description,
		List<Option> options
	) {
		public static ProductOptionInfo of(Product product, List<ProductOption> productOptions) {
			List<Option> options = productOptions.stream()
				.map(Option::of)
				.toList();

			return new ProductOptionInfo(
				product.getId(),
				product.getName(),
				product.getCategory(),
				product.getDescription(),
				options
			);
		}
	}

	public record Option(
		Long productOptionId,
		String optionName,
		Long price,
		int stock
	) {
		public static Option of(ProductOption productOption) {
			return new Option(
				productOption.getId(),
				productOption.getName(),
				productOption.getPrice(),
				productOption.getStock()
			);
		}
	}
}