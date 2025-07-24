package kr.hhplus.be.server.config.jpa.product.adapter.in;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.product.adapter.in.dto.ProductDetailsResponse;
import kr.hhplus.be.server.config.jpa.product.adapter.in.dto.RankProductDetailsResponse;

@Tag(name="상품", description = "상품 관련 API")
public interface ProductApiSpec {

	@Operation(summary = "상품 디테일 조회", description = "상품 아이디를 입려받아 상품 디테일을 반환합니다.")
	ResponseEntity<CommonResponse<ProductDetailsResponse>> getProductDetails(
		@Parameter(description = "상품 아이디", required = true) @PathVariable @PositiveOrZero Long productId);

	@Operation(summary = "인기 상품 조회", description = "인기 상품 5개를 조회합니다.")
	ResponseEntity<CommonResponse<List<RankProductDetailsResponse>>> getProductRank();
}