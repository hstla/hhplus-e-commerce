package kr.hhplus.be.domain.order.infrastructure;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.order.model.OrderProduct;
import kr.hhplus.be.domain.order.repository.OrderProductRepository;
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