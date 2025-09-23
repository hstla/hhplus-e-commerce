package kr.hhplus.be.application.order.listener;

import static org.springframework.transaction.annotation.Propagation.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.domain.common.event.CouponUseFailedEvent;
import kr.hhplus.be.domain.common.event.StockDecreaseFailedEvent;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderStatus;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderFailureEventListener {

	private final OrderRepository orderRepository;

	// 재고 처리 실패 이벤트를 수신하는 메서드
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	@Transactional(propagation = REQUIRES_NEW)
	public void handleStockFailure(StockDecreaseFailedEvent event) {
		log.info("재고 처리 실패 감지, 주문 상태 FAILED로 변경: orderId={}", event.orderId());
		updateOrderStatusToFailed(event.orderId());
	}

	// 쿠폰 처리 실패 이벤트를 수신하는 메서드
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	@Transactional(propagation = REQUIRES_NEW)
	public void handleCouponFailure(CouponUseFailedEvent event) {
		log.info("쿠폰 처리 실패 감지, 주문 상태 FAILED로 변경: orderId={}", event.orderId());
		updateOrderStatusToFailed(event.orderId());
	}

	private void updateOrderStatusToFailed(Long orderId) {
		Order order = orderRepository.findById(orderId);

		if (order.getStatus() == OrderStatus.PENDING) {
			order.markAsFailed();
			orderRepository.save(order);
		}
	}
}