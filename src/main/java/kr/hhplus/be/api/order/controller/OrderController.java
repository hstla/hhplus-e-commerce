package kr.hhplus.be.api.order.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import kr.hhplus.be.api.order.controller.dto.OrderRequest;
import kr.hhplus.be.api.order.controller.dto.OrderResponse;
import kr.hhplus.be.api.order.usecase.CreateOrderUseCase;
import kr.hhplus.be.api.order.usecase.GetOrderUseCase;
import kr.hhplus.be.api.order.usecase.PublisherOrderUseCase;
import kr.hhplus.be.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApiSpec {

	// private final CreateOrderUseCase orderUseCase;
	private final PublisherOrderUseCase orderUseCase;
	private final GetOrderUseCase getOrderUseCase;

	@Override
	@GetMapping("/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponse.Order>> getOrder(Long orderId) {
		OrderResult.Order order = getOrderUseCase.execute(orderId);
		return ResponseEntity.ok(CommonResponse.success(OrderResponse.Order.of(order)));
	}

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<OrderResponse.Order>> createOrder(OrderRequest.Order orderRequest) {
		OrderResult.Order order = orderUseCase.execute(orderRequest.toCommand());

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(order.id())
			.toUri();

		return ResponseEntity.accepted().location(location).
			body(CommonResponse.success(OrderResponse.Order.of(order)));
	}
}