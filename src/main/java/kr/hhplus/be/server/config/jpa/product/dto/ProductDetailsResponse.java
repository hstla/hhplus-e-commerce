package kr.hhplus.be.server.config.jpa.product.dto;

import java.util.List;

public record ProductDetailsResponse(
	Long productId,
	String productName,
	String productDescription,
	List<ProductOptionResponse> productOptions
) {
}
