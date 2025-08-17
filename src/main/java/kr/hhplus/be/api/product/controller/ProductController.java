package kr.hhplus.be.api.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.api.product.controller.dto.ProductResponse;
import kr.hhplus.be.api.product.usecase.FindProductUseCase;
import kr.hhplus.be.api.product.usecase.dto.ProductResult;
import kr.hhplus.be.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController implements ProductApiSpec {

	private final FindProductUseCase findProductUseCase;

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<CommonResponse<ProductResponse.ProductOptions>> getProductDetails(Long productId) {
		ProductResult.ProductOptionInfo findProductOptions = findProductUseCase.findProductOptionsById(productId);
		return ResponseEntity.ok(CommonResponse.success(ProductResponse.ProductOptions.of(findProductOptions.productId(),
			findProductOptions.productName(), findProductOptions.description(), findProductOptions.category(), findProductOptions.options()
		)));
	}
}