package kr.hhplus.be.domain.order.infrastructure;

import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import kr.hhplus.be.config.RepositoryTestConfig;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderStatus;

@Import({OrderCoreRepository.class})
@DisplayName("OrderCoreRepository 테스트")
class OrderCoreRepositoryTest  extends RepositoryTestConfig {

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
			assertSoftly(soft -> {
				soft.assertThat(savedOrder.getId()).isNotNull();
				soft.assertThat(savedOrder.getUserId()).isEqualTo(userId);
				soft.assertThat(savedOrder.getUserCouponId()).isEqualTo(userCouponId);
				soft.assertThat(savedOrder.getTotalPrice()).isEqualTo(totalPrice);
				soft.assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.AWAITING_PAYMENT);
				soft.assertThat(savedOrder.getOrderAt()).isEqualToIgnoringNanos(orderAt);
			});
		}
	}
}