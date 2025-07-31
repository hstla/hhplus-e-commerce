package kr.hhplus.be.server.config.jpa.api.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.config.jpa.api.order.controller.dto.OrderRequest;
import kr.hhplus.be.server.config.jpa.api.order.controller.dto.OrderResponse;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;

@Tag(name="주문", description = "주문 관련 API")
public interface OrderApiSpec {

	@Operation(summary = "주문 생성", description = "상품, 상품 옵션, 유저 정보를 받아 주문을 생성합니다.")
	ResponseEntity<CommonResponse<OrderResponse.Order>> createOrder(
		@Parameter(description = "상품 옵션, 아이디, 쿠폰 사용 여부", required = true) @RequestBody @Valid OrderRequest.Order createOrderRequest
	);
}