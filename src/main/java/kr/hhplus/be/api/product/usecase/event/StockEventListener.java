package kr.hhplus.be.api.product.usecase.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.api.product.usecase.helper.ProductOptionStockSpinLockManager;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.domain.shared.event.OrderCreatedEvent;
import kr.hhplus.be.domain.shared.event.StockDecreaseFailedEvent;
import kr.hhplus.be.domain.shared.event.StockDecreasedEvent;
import kr.hhplus.be.domain.shared.event.dto.OrderRequestItemInfo;
import kr.hhplus.be.domain.shared.event.dto.PricedOrderItemInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventListener {

	private final ProductOptionStockSpinLockManager stockManager;
	private final ProductOptionRepository productOptionRepository;
	private final ApplicationEventPublisher eventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleOrderCreated(OrderCreatedEvent event) {
		log.info("상품 재고 감소 로직 시작");
		Map<Long, Integer> optionQuantities = event.orderItems().stream()
			.collect(Collectors.toMap(
				OrderRequestItemInfo::productOptionId,
				OrderRequestItemInfo::quantity
			));

		try {
			List<PricedOrderItemInfo> pricedItems = new ArrayList<>();
			long totalOriginalPrice = 0L;

			for (OrderRequestItemInfo item : event.orderItems()) {
				ProductOption option = productOptionRepository.findById(item.productOptionId());

				PricedOrderItemInfo pricedItem = new PricedOrderItemInfo(
					option.getId(),
					option.getProductId(),
					item.quantity(),
					option.getPrice(),
					option.getName()
				);
				pricedItems.add(pricedItem);
				totalOriginalPrice += pricedItem.calculateItemTotalPrice();
			}

			// 분산 락 트랜잭션 시작
			stockManager.decreaseStockWithMultiSpinLock(optionQuantities);

			eventPublisher.publishEvent(new StockDecreasedEvent(
				event.orderId(),
				event.userId(),
				event.userCouponId(),
				totalOriginalPrice,
				pricedItems
			));

		} catch (Exception e) {
			log.warn("상품 재고 감소 실패: {}", e);
			eventPublisher.publishEvent(new StockDecreaseFailedEvent(event.orderId()));
		}
	}
}