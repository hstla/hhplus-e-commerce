package kr.hhplus.be.server.config.jpa.api.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.config.jpa.api.product.controller.dto.ProductResponse;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;

@Tag(name="상품", description = "상품 관련 API")
public interface ProductApiSpec {

	@Operation(summary = "상품 디테일 조회", description = "상품 아이디를 입려받아 상품 디테일을 반환합니다.")
	ResponseEntity<CommonResponse<ProductResponse.ProductOptions>> getProductDetails(
		@Parameter(description = "상품 아이디", required = true) @PathVariable @PositiveOrZero Long productId);
}