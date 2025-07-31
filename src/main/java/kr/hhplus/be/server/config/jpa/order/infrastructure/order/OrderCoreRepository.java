package kr.hhplus.be.server.config.jpa.order.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

	private final JpaOrderRepository jpaOrderRepository;
	private final OrderMapper orderMapper;

	@Override
	public Order save(Order order) {
		return null;
	}

	@Override
	public Optional<Order> findById(Long orderId) {
		return Optional.empty();
	}
}
