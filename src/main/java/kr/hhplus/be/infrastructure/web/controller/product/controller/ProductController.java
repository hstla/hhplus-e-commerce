package kr.hhplus.be.infrastructure.web.controller.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.application.product.dto.ProductResult;
import kr.hhplus.be.application.product.usecase.FindProductUseCase;
import kr.hhplus.be.application.product.usecase.RankProductUseCase;
import kr.hhplus.be.global.common.CommonResponse;
import kr.hhplus.be.infrastructure.web.controller.product.controller.dto.ProductResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController implements ProductApiSpec {

	private final FindProductUseCase findProductUseCase;
	private final RankProductUseCase rankProductUseCase;

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<CommonResponse<ProductResponse.ProductOptions>> getProductDetails(Long productId) {
		ProductResult.ProductOptionInfo findProductOptions = findProductUseCase.findProductOptionsById(productId);
		return ResponseEntity.ok(CommonResponse.success(ProductResponse.ProductOptions.of(findProductOptions.productId(),
			findProductOptions.productName(), findProductOptions.description(), findProductOptions.category(), findProductOptions.options()
		)));
	}

	@Override
	@GetMapping("/rank/{top5}")
	public ResponseEntity<CommonResponse<List<ProductResponse.ProductRank>>> getProductRank5() {
		List<ProductResponse.ProductRank> top5Products = rankProductUseCase.getTop5Products();
		return ResponseEntity.ok(CommonResponse.success(top5Products));
	}
}