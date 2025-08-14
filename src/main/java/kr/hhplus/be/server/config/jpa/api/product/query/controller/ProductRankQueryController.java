package kr.hhplus.be.server.config.jpa.api.product.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.config.jpa.api.product.query.dto.ProductRankDto;
import kr.hhplus.be.server.config.jpa.api.product.query.service.ProductRankQueryService;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name="상품랭킹", description = "상품랭킹 관련 API")
public class ProductRankQueryController {

	private final ProductRankQueryService service;

	@GetMapping("/api/product/rank")
	@Operation(summary = "상품 랭킹 조회", description = "3일간 인기상품 5가지를 조회합니다")
	public ResponseEntity<CommonResponse<List<ProductRankDto>>> getProductRank() {
		return ResponseEntity.ok(CommonResponse.success(service.getTop5ProductRank()));
	}
}