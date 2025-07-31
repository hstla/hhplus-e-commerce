package kr.hhplus.be.server.config.jpa.order.infrastructure.order;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.error.OrderErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.order.infrastructure.OrderMapper;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

	private final JpaOrderRepository jpaOrderRepository;
	private final OrderMapper orderMapper;

	@Override
	public Order save(Order order) {
		OrderEntity save = jpaOrderRepository.save(orderMapper.toEntity(order));
		return orderMapper.toModel(save);
	}

	@Override
	public Order findById(Long orderId) {
		OrderEntity findOrderOptional = jpaOrderRepository.findById(orderId).orElseThrow(() ->
			new RestApiException(OrderErrorCode.INACTIVE_ORDER));

		return orderMapper.toModel(findOrderOptional);
	}
}