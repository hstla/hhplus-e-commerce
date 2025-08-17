package kr.hhplus.be.api.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.api.order.controller.dto.OrderRequest;
import kr.hhplus.be.api.order.controller.dto.OrderResponse;
import kr.hhplus.be.api.order.usecase.CreateOrderUseCase;
import kr.hhplus.be.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApiSpec {

	private final CreateOrderUseCase orderUseCase;

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<OrderResponse.Order>> createOrder(OrderRequest.Order orderRequest) {
		OrderResult.Order order = orderUseCase.execute(orderRequest.toCommand());
		return ResponseEntity.ok(CommonResponse.success(OrderResponse.Order.of(order)));
	}
}
