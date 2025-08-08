package kr.hhplus.be.server.config.jpa.order.infrastructure;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.TestcontainersConfig;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import kr.hhplus.be.server.config.jpa.order.model.ProductOptionSnapshot;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({OrderProductCoreRepository.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("OrderProductCoreRepository 테스트")
class OrderProductCoreRepositoryTest {
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

		@Test
		@DisplayName("OrderProduct 저장에 성공한다")
		void save_success() {
			// given
			OrderProduct orderProduct = OrderProduct.create(testOrderId, ProductOptionSnapshot.create(name, quantity, unitPrice));

			// when
			OrderProduct saved = orderProductCoreRepository.save(orderProduct);

			// then
			assertThat(saved.getOrderId()).isEqualTo(testOrderId);
			assertThat(saved.getProductOptionVO().getName()).isEqualTo(name);
			assertThat(saved.getProductOptionVO().getStock()).isEqualTo(quantity);
			assertThat(saved.getProductOptionVO().getPrice()).isEqualTo(unitPrice);
		}
	}
}