package kr.hhplus.be.server.config.jpa.order.adapter.in;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.order.domain.OrderStatus;
import kr.hhplus.be.server.config.jpa.order.adapter.in.dto.CreateOrderRequest;
import kr.hhplus.be.server.config.jpa.order.adapter.in.dto.OrderResponse;

@RestController
@RequestMapping("/api/orders")
public class OrderController implements OrderApiSpec {

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<OrderResponse>> createOrder(CreateOrderRequest createOrderRequest) {
		return ResponseEntity.ok(CommonResponse.success(
			new OrderResponse(1L, 10000, 8000, OrderStatus.CREATED, LocalDateTime.now())));
	}
}
