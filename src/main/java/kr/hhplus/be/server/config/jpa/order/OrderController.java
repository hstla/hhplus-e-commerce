package kr.hhplus.be.server.config.jpa.order;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kr.hhplus.be.server.config.jpa.order.dto.CreateOrderRequest;
import kr.hhplus.be.server.config.jpa.order.dto.OrderResponse;

@RestController
@RequestMapping("/api/order")
public class OrderController implements OrderApiSpec {

	@Override
	@PostMapping
	public ResponseEntity<OrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
		return ResponseEntity.ok(new OrderResponse(1L, 10000, 8000, OrderStatus.CREATED, LocalDateTime.now()));
	}
}
