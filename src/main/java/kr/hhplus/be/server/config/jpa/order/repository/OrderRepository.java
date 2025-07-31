package kr.hhplus.be.server.config.jpa.order.repository;

import kr.hhplus.be.server.config.jpa.order.model.Order;

public interface OrderRepository {
	Order save(Order order);
	Order findById(Long orderId);
}
