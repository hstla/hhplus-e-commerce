package kr.hhplus.be.server.config.jpa.product.adapter.in;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.product.adapter.in.dto.ProductDetailsResponse;
import kr.hhplus.be.server.config.jpa.product.adapter.in.dto.RankProductDetailsResponse;
import kr.hhplus.be.server.config.jpa.product.domain.Product;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController implements ProductApiSpec {

	private FindProductUseCase findProductUseCase;

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<CommonResponse<ProductDetailsResponse>> getProductDetails(Long productId) {
		Product productById = findProductUseCase.findProductById(productId);
		return ResponseEntity.ok(CommonResponse.success(ProductDetailsResponse.of(productById)));
	}

	@Override
	@GetMapping("/ranks")
	public ResponseEntity<CommonResponse<List<RankProductDetailsResponse>>> getProductRank() {
		List<Product> top5Products = findProductUseCase.findTop5Products();

		return ResponseEntity.ok(CommonResponse.success(top5Products.stream().map(RankProductDetailsResponse::of).toList()));
	}
}