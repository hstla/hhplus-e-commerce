package kr.hhplus.be.server.config.jpa.order.domain;

import java.util.Optional;

public interface OrderRepository {
	Order save(Order order);
	Optional<Order> findById(Long orderId);
}
