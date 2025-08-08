package kr.hhplus.be.server.config.jpa.order.infrastructure;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderProductCoreRepository implements OrderProductRepository {

	private final JpaOrderProductRepository jpaOrderProductRepository;

	@Override
	public OrderProduct save(OrderProduct orderProduct) {
		return jpaOrderProductRepository.save(orderProduct);
	}
}