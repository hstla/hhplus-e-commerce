package kr.hhplus.be.server.config.jpa.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.product.dto.ProductDetailsResponse;
import kr.hhplus.be.server.config.jpa.product.dto.ProductOptionResponse;

@RestController
@RequestMapping("/api/product")
public class ProductController implements ProductApiSpec {

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<ProductDetailsResponse> getProductDetails(Long productId) {
		ProductOptionResponse productOption1 = new ProductOptionResponse(1L,"김풍", "보스", 2_000L, 50);
		ProductOptionResponse productOption2 = new ProductOptionResponse(2L,"빠니보틀", "행동대장", 1_000L, 100);

		return ResponseEntity.ok(new ProductDetailsResponse(1L, "파김치갱", "갱갱갱", List.of(productOption1, productOption2)));
	}

	@Override
	@GetMapping("/rank")
	public ResponseEntity<List<ProductDetailsResponse>> getProductRank() {
		ProductOptionResponse productOption1 = new ProductOptionResponse(1L, "김풍", "보스", 2_000L, 50);
		ProductOptionResponse productOption2 = new ProductOptionResponse(2L, "빠니보틀", "행동대장", 1_000L, 100);
		ProductOptionResponse productOption3 = new ProductOptionResponse(3L, "곽준빈", "브레인", 1_000L, 100);
		ProductOptionResponse productOption4 = new ProductOptionResponse(4L, "키드밀리", "글로벌 마케터", 2_000L, 50);

		ProductDetailsResponse productDetailsResponse1 = new ProductDetailsResponse(1L, "파김치갱", "갱갱갱",
			List.of(productOption1, productOption2));
		ProductDetailsResponse productDetailsResponse2 = new ProductDetailsResponse(1L, "파김치갱", "갱갱갱갱",
			List.of(productOption3, productOption4));

		return ResponseEntity.ok(List.of(productDetailsResponse1, productDetailsResponse2));
	}
}