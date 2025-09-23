package kr.hhplus.be.application.product.listener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.application.product.service.ProductStockService;
import kr.hhplus.be.domain.common.event.OrderCreatedEvent;
import kr.hhplus.be.domain.common.event.StockDecreaseFailedEvent;
import kr.hhplus.be.domain.common.event.StockDecreasedEvent;
import kr.hhplus.be.domain.common.event.dto.OrderRequestItemInfo;
import kr.hhplus.be.domain.common.event.dto.PricedOrderItemInfo;
import kr.hhplus.be.domain.product.model.ProductOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventListener {

	private final ProductStockService stockService;
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
			Map<Long, ProductOption> decreasedOptions = stockService.decreaseStock(optionQuantities);

			List<PricedOrderItemInfo> pricedItems = decreasedOptions.entrySet().stream()
				.map(entry -> {
					ProductOption option = entry.getValue();
					int quantity = optionQuantities.get(option.getId());
					return new PricedOrderItemInfo(
						option.getId(),
						option.getProductId(),
						quantity,
						option.getPrice(),
						option.getName()
					);
				})
				.toList();

			long totalOriginalPrice = pricedItems.stream()
				.mapToLong(PricedOrderItemInfo::calculateItemTotalPrice)
				.sum();

			eventPublisher.publishEvent(new StockDecreasedEvent(
				event.orderId(),
				event.userId(),
				event.userCouponId(),
				totalOriginalPrice,
				pricedItems
			));
		} catch (Exception e) {
			log.warn("상품 재고 감소 실패: {}", e.getMessage(), e);
			eventPublisher.publishEvent(new StockDecreaseFailedEvent(event.orderId()));
		}
	}
}