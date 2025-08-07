package kr.hhplus.be.server.config.jpa.order.infrastructure.order;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import java.time.LocalDateTime;

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
import kr.hhplus.be.server.config.jpa.error.OrderErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.server.config.jpa.order.infrastructure.OrderCoreRepository;
import kr.hhplus.be.server.config.jpa.order.infrastructure.OrderMapper;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({OrderCoreRepository.class, OrderMapper.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("OrderCoreRepository 테스트")
class OrderCoreRepositoryTest {

	@Autowired
	private JpaOrderRepository jpaOrderRepository;
	@Autowired
	private OrderCoreRepository orderCoreRepository;

	@BeforeEach
	void globalSetUp() {
		jpaOrderRepository.deleteAll();
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		@Test
		@DisplayName("새로운 Order 도메인 모델을 성공적으로 저장해야 한다")
		void save_success() {
			// given
			Long userId = 1L;
			Long userCouponId = 100L;
			Long totalPrice = 10_000L;
			LocalDateTime orderAt = LocalDateTime.now();
			Order newOrder = Order.create(userId, userCouponId, totalPrice, orderAt);

			// when
			Order savedOrder = orderCoreRepository.save(newOrder);

			// then
			assertThat(savedOrder.getId()).isNotNull();
			assertThat(savedOrder.getUserId()).isEqualTo(userId);
			assertThat(savedOrder.getUserCouponId()).isEqualTo(userCouponId);
			assertThat(savedOrder.getTotalPrice()).isEqualTo(totalPrice);
			assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
			assertThat(savedOrder.getOrderAt()).isEqualToIgnoringNanos(orderAt);
		}

		@Test
		@DisplayName("기존 Order 도메인 모델의 상태를 성공적으로 업데이트해야 한다")
		void save_update() {
			// given
			Long userId = 2L;
			Long userCouponId = 3L;
			LocalDateTime orderAt = LocalDateTime.now().minusHours(1);
			OrderEntity existingEntity = new OrderEntity(userId, userCouponId, 10_000L, OrderStatus.CREATED, orderAt);
			OrderEntity savedOriginalEntity = jpaOrderRepository.save(existingEntity);

			Order orderToUpdate = new Order(savedOriginalEntity.getId(), userId, userCouponId, 10_000L, OrderStatus.PAID, orderAt);

			// when
			Order updatedOrder = orderCoreRepository.save(orderToUpdate);

			// then
			assertThat(updatedOrder.getId()).isEqualTo(savedOriginalEntity.getId());
			assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
		}
	}

	@Nested
	@DisplayName("findById 메서드 테스트")
	class FindByIdTests {

		private OrderEntity savedOrderEntity;
		private Long testUserId = 3L;
		private Long testUserCouponId = 200L;
		private LocalDateTime testOrderAt = LocalDateTime.now().minusDays(1);

		@BeforeEach
		void setUp() {
			// 테스트를 위한 OrderEntity 저장
			OrderEntity testEntity = new OrderEntity(testUserId, testUserCouponId, 10_000L, OrderStatus.CREATED, testOrderAt);
			savedOrderEntity = jpaOrderRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 ID로 조회 시 Order 도메인 모델을 반환해야 한다")
		void findById() {
			// when
			Order foundOrder = orderCoreRepository.findById(savedOrderEntity.getId());

			// then
			assertThat(foundOrder.getId()).isEqualTo(savedOrderEntity.getId());
			assertThat(foundOrder.getUserId()).isEqualTo(testUserId);
			assertThat(foundOrder.getUserCouponId()).isEqualTo(testUserCouponId);
			assertThat(foundOrder.getOrderAt()).isEqualToIgnoringNanos(testOrderAt);
			assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
		}

		@Test
		@DisplayName("존재하지 않는 ID로 조회 시 INACTIVE_ORDER 에러를 반환해야 한다")
		void findById_fail_inactiveOrder() {
			// given
			Long nonExistentId = 999L;

			// when then
			assertThatThrownBy(() -> orderCoreRepository.findById(nonExistentId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(OrderErrorCode.INACTIVE_ORDER.getMessage());
		}
	}
}