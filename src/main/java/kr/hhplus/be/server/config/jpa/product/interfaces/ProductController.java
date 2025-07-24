package kr.hhplus.be.server.config.jpa.product.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.product.application.ProductFacade;
import kr.hhplus.be.server.config.jpa.product.application.ProductResult;
import kr.hhplus.be.server.config.jpa.product.interfaces.dto.ProductResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController implements ProductApiSpec {

	private final ProductFacade productFacade;

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<CommonResponse<ProductResponse.ProductOptions>> getProductDetails(Long productId) {
		ProductResult.ProductOption findProductOptions = productFacade.findProductOptionsById(productId);
		return ResponseEntity.ok(CommonResponse.success(ProductResponse.ProductOptions.of(findProductOptions.getProductId(),
			findProductOptions.getProductName(), findProductOptions.getDescription(), findProductOptions.getCategory(), findProductOptions.getOptions()
		)));
	}

	@Override
	@GetMapping("/ranks")
	public ResponseEntity<CommonResponse<List<ProductResponse.ProductRankList>>> getProductRank() {
		// List<Product> top5Products = findProductUseCase.findTop5Products();
		// return ResponseEntity.ok(CommonResponse.success(top5Products.stream().map(RankProductDetailsResponse::of).toList()));
		return null;
	}
}