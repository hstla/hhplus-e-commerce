package kr.hhplus.be.server.config.jpa.order.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.order.domain.Order;
import kr.hhplus.be.server.config.jpa.order.domain.OrderRepository;

@Component
public class OrderRepositoryImpl implements OrderRepository {
	@Override
	public Order save(Order order) {
		return null;
	}

	@Override
	public Optional<Order> findById(Long orderId) {
		return Optional.empty();
	}
}
