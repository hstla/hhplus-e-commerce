package kr.hhplus.be.application.order.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.application.order.dto.OrderResult;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.infrastructure.persistence.order.JpaOrderRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@DisplayName("GetOrderUseCase 통합 테스트")
class GetOrderUseCaseTest extends IntegrationTestConfig {

	@Autowired
	private GetOrderUseCase getOrderUseCase;
	@Autowired
	private JpaOrderRepository orderRepository;

	Order savedOrder;

	@BeforeEach
	void setUp() {
		orderRepository.deleteAll();
		savedOrder = orderRepository.save(Order.create(1L, null, 10_000L, 0L, 10_000L, LocalDateTime.now()));
	}

	@Nested
	@DisplayName("주문 찾기 성공 케이스")
	class SuccessCase {

		@Test
		@DisplayName("유효한 주문 번호를 받아 주문 정보를 반환한다.")
		void getOrder_success() {
			// given
			Long orderId = savedOrder.getId();

			// when
			OrderResult.Order findOrder = getOrderUseCase.execute(orderId);

			// then
			assertSoftly(soft -> {
				soft.assertThat(findOrder.id()).isEqualTo(savedOrder.getId());
				soft.assertThat(findOrder.totalPrice()).isEqualTo(savedOrder.getTotalPrice());
			});
		}
	}

	@Nested
	@DisplayName("주문 찾기 실패 케이스")
	class FailureCase {

		@Test
		@DisplayName("유효하지 않는 주문 번호를 받아 주문 정보를 반환에 실패한다.")
		void getOrder_success() {
			// given
			Long orderId = 999L;

			// when then
			assertThatThrownBy(() -> getOrderUseCase.execute(orderId))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Order is inactive");
		}
	}
}