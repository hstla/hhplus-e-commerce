package kr.hhplus.be.application.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.config.ConcurrentTestSupport;
import kr.hhplus.be.domain.common.event.dto.PricedOrderItemInfo;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.infrastructure.persistence.product.JpaProductOptionRepository;
import kr.hhplus.be.infrastructure.persistence.product.JpaProductRepository;

@DisplayName("ProductStockServiceTest 동시성 테스트")
class ProductStockServiceTest extends ConcurrentTestSupport {
	@Autowired
	private ProductStockService productStockService;
	@Autowired
	private JpaProductOptionRepository jpaProductOptionRepository;
	@Autowired
	private JpaProductRepository jpaProductRepository;

	Product savedProduct1;
	ProductOption option1;
	ProductOption option2;

	@BeforeEach
	public void setup() {
		jpaProductOptionRepository.deleteAll();
		jpaProductRepository.deleteAll();

		Product product1 = Product.create("Test Product1", ProductCategory.FOOD, "Description");
		savedProduct1 = jpaProductRepository.save(product1);

		option1 = ProductOption.create(savedProduct1.getId(), "Option 1", 1_000L, 10);
		option2 = ProductOption.create(savedProduct1.getId(), "Option 2", 1_500L, 5);
		jpaProductOptionRepository.saveAll(List.of(option1, option2));
	}

	@Nested
	@DisplayName("정상 케이스")
	class SuccessCase {

		@Test
		@DisplayName("단일 옵션 재고 감소")
		void decreaseStock_singleOption() {
			// given
			ProductOption option = jpaProductOptionRepository.findById(option1.getId()).get();
			int quantityToDecrease = 3;

			// when
			// List<PricedOrderItemInfo> result = productStockService.decreaseStock(
			// 	Map.of(option.getId(), quantityToDecrease));

			Map<Long, ProductOption> longProductOptionMap = productStockService.decreaseStock(
				Map.of(option.getId(), quantityToDecrease));
			// then
			ProductOption result2 = longProductOptionMap.get(option.getId());
			assertThat(result2.getStock()).isEqualTo(option.getStock() - quantityToDecrease);
		}

		@Test
		@DisplayName("여러 옵션 동시에 재고 감소")
		void decreaseStock_multipleOptions() {
			// given
			Map<Long, Integer> decreaseMap = Map.of(
				option1.getId(), 2,
				option2.getId(), 1
			);

			// when
			productStockService.decreaseStock(decreaseMap);

			// then
			var result1 = jpaProductOptionRepository.findById(option1.getId()).get();
			var result2 = jpaProductOptionRepository.findById(option2.getId()).get();

			assertThat(result1.getStock()).isEqualTo(option1.getStock() - 2);
			assertThat(result2.getStock()).isEqualTo(option2.getStock() - 1);
		}
	}

	@Nested
	@DisplayName("동시성 테스트")
	class ConcurrentSuccessCase {

		@Test
		@DisplayName("여러 스레드에서 동시에 재고 감소")
		void concurrentDecreaseStock() throws InterruptedException {
			// given
			ProductOption option = jpaProductOptionRepository.findById(option1.getId()).get();
			int initialStock = option.getStock();
			int threadCount = 10;
			int orderQuantityPerThread = 2;

			// when
			ConcurrentTestResult<Void> result = runConcurrentTest(
				threadCount,
				() -> {
					productStockService.decreaseStock(Map.of(option.getId(), orderQuantityPerThread));
					return null;
				}
			);

			// then
			ProductOption updatedOption = jpaProductOptionRepository.findById(option.getId()).get();

			assertSoftly(soft -> {
				soft.assertThat(updatedOption.getStock()).isEqualTo(Math.max(0, initialStock - threadCount * orderQuantityPerThread));
				soft.assertThat(result.exceptions().size()).isEqualTo(5);
			});
		}
	}

	@Nested
	@DisplayName("롤백 테스트")
	class RollBackCase {

		@Test
		@DisplayName("재고 감소 후 보상 처리")
		void compensateStock_shouldRestoreStock() {
			// given
			ProductOption option = jpaProductOptionRepository.findById(option1.getId()).get();
			int initialStock = option.getStock();
			Map<Long, Integer> decreaseMap = Map.of(option.getId(), 3);

			// when
			productStockService.decreaseStock(decreaseMap);
			productStockService.compensateStocks(decreaseMap);

			// then
			ProductOption restoredOption = jpaProductOptionRepository.findById(option.getId()).get();
			assertThat(restoredOption.getStock()).isEqualTo(initialStock);
		}
	}
}