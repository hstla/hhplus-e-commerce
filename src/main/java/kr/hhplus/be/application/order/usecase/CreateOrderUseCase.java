package kr.hhplus.be.application.order.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.application.order.dto.OrderCommand;
import kr.hhplus.be.application.order.dto.OrderResult;
import kr.hhplus.be.domain.common.event.OrderCreatedEvent;
import kr.hhplus.be.domain.common.event.dto.OrderRequestItemInfo;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.order.service.dto.OrderInfo;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateOrderUseCase {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public OrderResult.Order execute(OrderCommand.Order command) {
		log.info("주문 생성 시작");
		if (!userRepository.assertUserExists(command.userId())) {
			throw new RestApiException(UserErrorCode.INACTIVE_USER);
		}

		Order pendingOrder = Order.createPending(
			command.userId(),
			command.userCouponId(),
			LocalDateTime.now()
		);
		Order savedOrder = orderRepository.save(pendingOrder);

		List<OrderRequestItemInfo> itemInfos = command.orderItemRequests().stream()
			.map(item -> new OrderRequestItemInfo(item.productOptionId(), item.quantity()))
			.toList();

		eventPublisher.publishEvent(new OrderCreatedEvent(
			savedOrder.getId(),
			command.userId(),
			command.userCouponId(),
			itemInfos
		));

		return OrderResult.Order.of(OrderInfo.OrderDetail.of(savedOrder));
	}
}