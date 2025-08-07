package kr.hhplus.be.server.config.jpa.order.infrastructure.order;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.error.OrderErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

	private final JpaOrderRepository jpaOrderRepository;

	@Override
	public Order save(Order order) {
		return jpaOrderRepository.save(order);
	}

	@Override
	public Order findById(Long orderId) {
		return jpaOrderRepository.findById(orderId).orElseThrow(() ->
			new RestApiException(OrderErrorCode.INACTIVE_ORDER));
	}
}