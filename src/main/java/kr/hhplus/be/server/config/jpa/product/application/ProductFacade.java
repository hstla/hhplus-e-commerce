package kr.hhplus.be.server.config.jpa.product.application;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.product.domain.ProductInfo;
import kr.hhplus.be.server.config.jpa.product.domain.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductFacade {

	private final ProductService productService;

	public ProductResult.ProductOption findProductOptionsById(Long productId) {
		ProductInfo.ProductOptionInfo products = productService.getProductOptionInfoById(productId);
		return ProductResult.ProductOption.of(products.getProductId(), products.getProductName(),
			products.getCategory(), products.getDescription(), products.getOptions());
	}
}