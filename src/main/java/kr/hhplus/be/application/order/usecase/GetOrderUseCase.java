package kr.hhplus.be.application.order.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.application.order.dto.OrderResult;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.order.service.dto.OrderInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

	private final OrderRepository orderRepository;

	@Transactional(readOnly = true)
	public OrderResult.Order execute(Long orderId) {
		Order findOrder = orderRepository.findById(orderId);

		return OrderResult.Order.of(OrderInfo.OrderDetail.of(findOrder));
	}
}