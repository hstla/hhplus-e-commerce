package kr.hhplus.be.api.order.usecase.helper;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.global.common.interfaces.DistributedSpinLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductOptionStockSpinLockManager {

	private final ProductOptionRepository productOptionRepository;
	private final TransactionTemplate transactionTemplate;

	@DistributedSpinLock(keys = "product_option_stock:#{#optionQuantities.keySet()}")
	@Transactional
	public Map<Long, ProductOption> decreaseStockWithMultiSpinLock(Map<Long, Integer> optionQuantities) {
		log.debug("재고 감소 시작: {}", optionQuantities);

		Map<Long, ProductOption> result = new LinkedHashMap<>();
		for (Map.Entry<Long, Integer> entry : optionQuantities.entrySet()) {
			Long optionId = entry.getKey();
			int quantity = entry.getValue();

			ProductOption option = productOptionRepository.findById(optionId);
			option.orderDecreaseStock(quantity);
			productOptionRepository.save(option);
			result.put(optionId, option);
		}

		log.debug("재고 감소 완료: {}", result.keySet());
		return result;
	}

	// 재고 감소한 제품옵션의 아이디와 수량을 받아 보상처리
	public void compensateStocks(Map<Long, Integer> lockedOptions) {
		for (Map.Entry<Long, Integer> entry : lockedOptions.entrySet()) {
			Long optionId = entry.getKey();
			Integer quantity = entry.getValue();
			log.warn("재고 보상 실행 optionId={}, quantity={}", optionId, quantity);
			increaseStock(optionId, quantity);
		}
	}

	// 재고 증가 (보상 트랜잭션)
	private void increaseStock(Long optionId, int quantity) {
		transactionTemplate.execute(status -> {
			ProductOption option = productOptionRepository.findById(optionId);
			option.increaseStock(quantity);
			productOptionRepository.save(option);
			return null;
		});
	}
}