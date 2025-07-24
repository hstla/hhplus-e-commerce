package kr.hhplus.be.server.config.jpa.order.infrastructure;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.order.domain.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.domain.OrderProductRepository;
import kr.hhplus.be.server.config.jpa.order.domain.OrderRepository;

@Component
public class OrderProductRepositoryImpl implements OrderProductRepository {

	@Override
	public List<OrderProduct> findByOrderId(Long orderId) {
		return List.of();
	}
}
