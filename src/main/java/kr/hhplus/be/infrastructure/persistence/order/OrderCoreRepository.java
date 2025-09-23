package kr.hhplus.be.infrastructure.persistence.order;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.global.error.OrderErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

	private final JpaOrderRepository jpaOrderRepository;

	@Override
	public Order save(Order order) {
		return jpaOrderRepository.save(order);
	}

	@Override
	public Order findById(Long orderId) {
		return jpaOrderRepository.findById(orderId).orElseThrow(() ->
			new RestApiException(OrderErrorCode.INACTIVE_ORDER));
	}
}