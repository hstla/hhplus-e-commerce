package kr.hhplus.be.api.product.usecase.event;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.domain.product.repository.ProductRankingRedisRepository;
import kr.hhplus.be.domain.shared.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRankingUpdateListener {

	private final ProductRankingRedisRepository productRankingRedisRepository;

	@Async
	@TransactionalEventListener
	public void updateProductRankingCache(OrderCompletedEvent event) {
		LocalDateTime now =  LocalDateTime.now();
		for (Map.Entry<Long, Integer> entry : event.productOrderCounts().entrySet()) {
			productRankingRedisRepository.updateProductSalesRanking(entry.getKey(), entry.getValue(), now);
		}
	}
}