package kr.hhplus.be.server.config.jpa.order.infrastructure;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderProductCoreRepository implements OrderProductRepository {

	private final JpaOrderProductRepository jpaOrderProductRepository;
	private final OrderMapper orderMapper;

	@Override
	public List<OrderProduct> findByOrderId(Long orderId) {
		return List.of();
	}
}
