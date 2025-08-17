package kr.hhplus.be.domain.order.repository;

import kr.hhplus.be.domain.order.model.Order;

public interface OrderRepository {
	Order save(Order order);
	Order findById(Long orderId);
}