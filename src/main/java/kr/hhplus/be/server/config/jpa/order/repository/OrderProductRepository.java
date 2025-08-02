package kr.hhplus.be.server.config.jpa.order.repository;

import java.util.List;

import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;

public interface OrderProductRepository {
	List<OrderProduct> findByOrderId(Long orderId);
	OrderProduct save(OrderProduct orderProduct);
}
