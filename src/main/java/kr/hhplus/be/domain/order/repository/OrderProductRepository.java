package kr.hhplus.be.domain.order.repository;

import kr.hhplus.be.domain.order.model.OrderProduct;

public interface OrderProductRepository {
	OrderProduct save(OrderProduct orderProduct);
}