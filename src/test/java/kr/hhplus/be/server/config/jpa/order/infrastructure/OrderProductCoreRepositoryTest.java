package kr.hhplus.be.server.config.jpa.order.infrastructure.orderproduct;

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
import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderProductRepository;
import kr.hhplus.be.server.config.jpa.order.infrastructure.OrderMapper;
import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.server.config.jpa.order.infrastructure.OrderProductCoreRepository;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({OrderProductCoreRepository.class, OrderMapper.class, TestcontainersConfig.class})
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
		OrderEntity testOrderEntity = new OrderEntity(1L, 1L, 10_000L, OrderStatus.CREATED, now);
		testOrderEntity = jpaOrderRepository.save(testOrderEntity);
		testOrderId = testOrderEntity.getId();

		OrderEntity anotherOrderEntity = new OrderEntity(1L, null, 20_000L, OrderStatus.CREATED, now);
		anotherOrderEntity = jpaOrderRepository.save(anotherOrderEntity);
		anotherOrderId = anotherOrderEntity.getId();
	}

	@Nested
	@DisplayName("findByOrderId 메서드 테스트")
	class FindByOrderIdTests {

		private Long testOptionId1 = 101L;
		private Long testOptionId2 = 102L;
		private Long testOptionId3 = 103L;

		@BeforeEach
		void setUp() {
			// testOrderId에 해당하는 OrderProductEntity들 저장
			jpaOrderProductRepository.save(new OrderProductEntity(testOrderId, testOptionId1, 10, 10_000L));
			jpaOrderProductRepository.save(new OrderProductEntity(testOrderId, testOptionId2, 20, 25_000L));
			// anotherOrderId에 해당하는 OrderProductEntity 저장
			jpaOrderProductRepository.save(new OrderProductEntity(anotherOrderId, testOptionId3, 10, 5_000L));
		}

		@Test
		@DisplayName("특정 orderId에 해당하는 모든 OrderProduct 도메인 모델을 반환해야 한다")
		void findByOrderId_success() {
			// when
			List<OrderProduct> foundOrderProducts = orderProductCoreRepository.findByOrderId(testOrderId);

			// then
			assertThat(foundOrderProducts).hasSize(2);
			assertThat(foundOrderProducts).extracting(OrderProduct::getOrderId).containsOnly(testOrderId);
			assertThat(foundOrderProducts).extracting(OrderProduct::getProductOptionId).containsExactlyInAnyOrder(testOptionId1, testOptionId2);

			assertThat(foundOrderProducts)
				.anySatisfy(op -> {
					assertThat(op.getProductOptionId()).isEqualTo(testOptionId1);
					assertThat(op.getQuantity()).isEqualTo(10);
					assertThat(op.getPrice()).isEqualTo(10_000L);
				})
				.anySatisfy(op -> {
					assertThat(op.getProductOptionId()).isEqualTo(testOptionId2);
					assertThat(op.getQuantity()).isEqualTo(20);
					assertThat(op.getPrice()).isEqualTo(25_000L);
				});
		}

		@Test
		@DisplayName("존재하지 않는 orderId로 조회 시 빈 리스트를 반환해야 한다")
		void findByOrderId_empty() {
			// given
			Long nonExistentOrderId = 999L;

			// when
			List<OrderProduct> foundOrderProducts = orderProductCoreRepository.findByOrderId(nonExistentOrderId);

			// then
			assertThat(foundOrderProducts).isEmpty();
		}
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		private final Long testProductOptionId = 201L;
		private final int quantity = 5;
		private final Long unitPrice = 3000L;

		@Test
		@DisplayName("OrderProduct 저장에 성공한다")
		void save_success() {
			// given
			OrderProduct orderProduct = OrderProduct.create(testOrderId, testProductOptionId, quantity, unitPrice);

			// when
			OrderProduct saved = orderProductCoreRepository.save(orderProduct);

			// then
			assertThat(saved.getOrderId()).isEqualTo(testOrderId);
			assertThat(saved.getProductOptionId()).isEqualTo(testProductOptionId);
			assertThat(saved.getQuantity()).isEqualTo(quantity);
			assertThat(saved.getPrice()).isEqualTo(unitPrice);
			assertThat(saved.getId()).isNotNull();
		}
	}
}