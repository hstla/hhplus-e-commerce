package kr.hhplus.be.application.product.listener;

import static org.springframework.transaction.annotation.Propagation.*;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.application.product.service.ProductStockService;
import kr.hhplus.be.domain.common.event.CouponUseFailedEvent;
import kr.hhplus.be.domain.common.event.dto.PricedOrderItemInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockFailureEventListener {

	private final ProductStockService stockManager;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	@Transactional(propagation = REQUIRES_NEW)
	public void updateStockToFailed(CouponUseFailedEvent event) {
		Map<Long, Integer> itemsToCompensate = event.productOptionItems().stream()
			.collect(Collectors.toMap(
				PricedOrderItemInfo::productOptionId,
				PricedOrderItemInfo::quantity
			));

		stockManager.compensateStocks(itemsToCompensate);
	}
}