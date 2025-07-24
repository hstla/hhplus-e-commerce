package kr.hhplus.be.server.config.jpa.order.interfaces;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.order.application.OrderFacade;
import kr.hhplus.be.server.config.jpa.order.application.OrderResult;
import kr.hhplus.be.server.config.jpa.order.interfaces.dto.OrderRequest;
import kr.hhplus.be.server.config.jpa.order.interfaces.dto.OrderResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApiSpec {

	private final OrderFacade orderFacade;

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<OrderResponse.Order>> createOrder(OrderRequest.Order orderRequest) {
		OrderResult.Order order = orderFacade.order(orderRequest.toCommand());
		return ResponseEntity.ok(CommonResponse.success(OrderResponse.Order.of(order)));
	}
}
