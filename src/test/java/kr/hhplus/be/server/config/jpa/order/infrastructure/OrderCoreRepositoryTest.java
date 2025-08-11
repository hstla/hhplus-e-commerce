package kr.hhplus.be.server.config.jpa.order.infrastructure;

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
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({OrderCoreRepository.class, TestcontainersConfig.class})
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
			Long originPrice = 10_000L;
			Long discountPrice = 2_000L;
			Long totalPrice = 8_000L;
			LocalDateTime orderAt = LocalDateTime.now();
			Order newOrder = Order.create(userId, userCouponId, originPrice, discountPrice, totalPrice, orderAt);

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
	}
}