package kr.hhplus.be.api.order.usecase.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import kr.hhplus.be.global.common.FullJitterBackOff;
import kr.hhplus.be.global.error.CommonErrorCode;
import kr.hhplus.be.global.error.ProductErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductOptionStockSpinLockManager {

	private final RedissonClient redissonClient;
	private final ProductOptionRepository productOptionRepository;
	private final TransactionTemplate transactionTemplate;

	private static final long LOCK_HOLD_SECONDS = 3;   // 락 보유 시간
	private static final long MAX_WAIT_MILLIS   = 5000; // 전체 대기 허용 시간

	public Map<Long, ProductOption> decreaseStockWithMultiSpinLock(Map<Long, Integer> optionQuantities) {
		List<RLock> locks = optionQuantities.keySet().stream()
			.map(id -> redissonClient.getSpinLock("product_option_stock:" + id))
			.toList();

		RedissonMultiLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));
		FullJitterBackOff backOff = new FullJitterBackOff(100, 1000);
		long deadline = System.currentTimeMillis() + MAX_WAIT_MILLIS;
		int attempt = 0;

		try {
			while (System.currentTimeMillis() < deadline) {
				if (multiLock.tryLock(0, LOCK_HOLD_SECONDS, TimeUnit.SECONDS)) {
					try {
						// 트랜잭션 모든 옵션 재고 감소
						return transactionTemplate.execute(status -> {
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
						});
					} finally {
						if (multiLock.isHeldByCurrentThread()) {
							multiLock.unlock();
						}
					}
				}
				long sleep = backOff.nextDelay(attempt++);
				Thread.sleep(sleep);
			}
			throw new RestApiException(ProductErrorCode.OUT_OF_STOCK);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RestApiException(CommonErrorCode.LOCK_ACQUIRE_FAILED);
		}
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