package kr.hhplus.be.application.product.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.global.common.aop.DistributedSpinLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductStockService {

	private final ProductOptionRepository productOptionRepository;

	@Transactional
	@DistributedSpinLock(keys = "product_option_stock:#{#optionQuantities.keySet()}")
	public Map<Long, ProductOption> decreaseStock(Map<Long, Integer> optionQuantities) {
		Map<Long, ProductOption> result = new LinkedHashMap<>();

		for (Map.Entry<Long, Integer> entry : optionQuantities.entrySet()) {
			Long optionId = entry.getKey();
			int quantity = entry.getValue();

			ProductOption option = productOptionRepository.findById(optionId);
			option.orderDecreaseStock(quantity);
			productOptionRepository.save(option);

			result.put(optionId, option);
		}
		return result;
	}

	// 재고 감소한 제품옵션의 아이디와 수량을 받아 보상처리
	@Transactional
	public void compensateStocks(Map<Long, Integer> lockedOptions) {
		for (Map.Entry<Long, Integer> entry : lockedOptions.entrySet()) {
			Long optionId = entry.getKey();
			Integer quantity = entry.getValue();

			increaseStock(optionId, quantity);
		}
	}

	private void increaseStock(Long optionId, int quantity) {
		ProductOption option = productOptionRepository.findById(optionId);
		option.increaseStock(quantity);
		productOptionRepository.save(option);
	}
}