package kr.hhplus.be.application.order.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.domain.common.event.CouponUsedEvent;
import kr.hhplus.be.domain.common.event.OrderCompletedEvent;
import kr.hhplus.be.domain.common.event.dto.PricedOrderItemInfo;
import kr.hhplus.be.domain.order.component.OrderPriceCalculator;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderProduct;
import kr.hhplus.be.domain.order.model.ProductOptionSnapshot;
import kr.hhplus.be.domain.order.repository.OrderProductRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompletionEventListener {

	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final OrderPriceCalculator orderPriceCalculator;
	private final ApplicationEventPublisher eventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void orderCompleteEvent(CouponUsedEvent event) {
		log.info("주문 성공 이벤트 시작, {}", event);

		Order order = orderRepository.findById(event.orderId());

		long totalPrice = orderPriceCalculator.calculateTotalPrice(
			event.totalOriginalPrice(),
			event.discountPrice()
		);

		order.markAsAwaitingPayment(
			event.totalOriginalPrice(),
			event.discountPrice(),
			totalPrice
		);
		orderRepository.save(order);

		Map<Long, Integer> productOrderCounts = new HashMap<>();

		for (PricedOrderItemInfo itemInfo : event.pricedOrderItems()) {
			OrderProduct orderProduct = OrderProduct.create(order.getId(), ProductOptionSnapshot.create(
				itemInfo.productOptionId(), itemInfo.name(), itemInfo.quantity(), itemInfo.price()));
			orderProductRepository.save(orderProduct);

			productOrderCounts.merge(itemInfo.productId(), itemInfo.quantity(), Integer::sum);
		}

		eventPublisher.publishEvent(new OrderCompletedEvent(event.orderId(), productOrderCounts));
	}
}