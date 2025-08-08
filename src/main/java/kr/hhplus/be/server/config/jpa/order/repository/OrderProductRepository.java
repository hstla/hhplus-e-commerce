package kr.hhplus.be.server.config.jpa.order.repository;

import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;

public interface OrderProductRepository {
	OrderProduct save(OrderProduct orderProduct);
}