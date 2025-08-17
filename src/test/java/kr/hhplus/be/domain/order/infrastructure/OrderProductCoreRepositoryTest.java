package kr.hhplus.be.domain.order.infrastructure;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import kr.hhplus.be.config.RepositoryTestConfig;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderProduct;
import kr.hhplus.be.domain.order.model.ProductOptionSnapshot;

@Import({OrderProductCoreRepository.class})
@DisplayName("OrderProductCoreRepository 테스트")
class OrderProductCoreRepositoryTest extends RepositoryTestConfig {
	@Autowired
	private JpaOrderProductRepository jpaOrderProductRepository;
	@Autowired
	private JpaOrderRepository jpaOrderRepository;
	@Autowired
	private OrderProductCoreRepository orderProductCoreRepository;

	private Long testOrderId;
	private Long anotherOrderId;

	@BeforeEach
	void globalSetUp() {
		jpaOrderProductRepository.deleteAll();
		jpaOrderRepository.deleteAll();

		LocalDateTime now = LocalDateTime.now();
		Order testOrderEntity = Order.create(1L, 100L, 10_000L, 0L, 10_000L, now.minusDays(1));
		testOrderEntity = jpaOrderRepository.save(testOrderEntity);
		testOrderId = testOrderEntity.getId();

		Order anotherOrderEntity = Order.create(2L, 200L, 10_000L, 2_000L, 8_000L, now.minusDays(1));
		anotherOrderEntity = jpaOrderRepository.save(anotherOrderEntity);
		anotherOrderId = anotherOrderEntity.getId();
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		private final String name = "snapshotName";
		private final int quantity = 5;
		private final Long unitPrice = 3000L;
		private final Long productOptionId = 1L;

		@Test
		@DisplayName("OrderProduct 저장에 성공한다")
		void save_success() {
			// given
			OrderProduct orderProduct = OrderProduct.create(testOrderId, ProductOptionSnapshot.create(productOptionId, name, quantity, unitPrice));

			// when
			OrderProduct saved = orderProductCoreRepository.save(orderProduct);

			// then
			assertAll(
				() -> assertThat(saved.getOrderId()).isEqualTo(testOrderId),
				() -> assertThat(saved.getProductOptionVO().getName()).isEqualTo(name),
				() -> assertThat(saved.getProductOptionVO().getStock()).isEqualTo(quantity),
				() -> assertThat(saved.getProductOptionVO().getPrice()).isEqualTo(unitPrice)
			);
		}
	}
}