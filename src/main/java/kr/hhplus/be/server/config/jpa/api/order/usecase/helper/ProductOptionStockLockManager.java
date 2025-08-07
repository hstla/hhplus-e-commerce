package kr.hhplus.be.server.config.jpa.api.order.usecase.helper;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductOptionStockLockManager {

	private final ProductOptionRepository productOptionRepository;

	@Transactional
	@Retryable(
		retryFor = {ObjectOptimisticLockingFailureException.class},
		maxAttempts = 5,
		backoff = @Backoff(delay = 100, multiplier = 2)
	)
	public ProductOption decreaseStockWithLock(Long optionId, int quantity) {
		ProductOption option = productOptionRepository.findById(optionId);
		option.orderDecreaseStock(quantity);
		return productOptionRepository.save(option);
	}
}