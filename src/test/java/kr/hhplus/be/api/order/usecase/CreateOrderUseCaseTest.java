package kr.hhplus.be.api.order.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderStatus;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.global.error.RestApiException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@DisplayName("CreateOrderUseCase 통합 테스트")
class CreateOrderUseCaseTest extends IntegrationTestConfig {

	@Autowired
	private CreateOrderUseCase createOrderUseCase;
	@Autowired
	private JpaOrderRepository orderRepository;
	@Autowired
	private JpaUserRepository userRepository;

	private User user;

	@BeforeEach
	void setUp() {
		orderRepository.deleteAll();
		userRepository.deleteAll();

		user = userRepository.save(User.create("name", "test@email.com", "123456789"));
	}

	@Nested
	@DisplayName("기본 주문 생성 성공 케이스")
	class SuccessCase {

		@Test
		@DisplayName("주문 생성 시 주문 상태는 PENDING 이어야 한다.")
		void createOrder_success() {
			// given
			OrderCommand.OrderProduct orderProduct1 = OrderCommand.OrderProduct.of(1L, 3);
			OrderCommand.OrderProduct orderProduct2 = OrderCommand.OrderProduct.of(2L, 2);
			List<OrderCommand.OrderProduct> orderProducts = Arrays.asList(orderProduct1, orderProduct2);
			OrderCommand.Order command = OrderCommand.Order.of(user.getId(), 0L, orderProducts);

			// when
			OrderResult.Order execute = createOrderUseCase.execute(command);

			// then
			Order findOrder = orderRepository.findById(execute.id()).get();

			assertSoftly(soft -> {
				soft.assertThat(execute.userId()).isEqualTo(user.getId());
				soft.assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
			});
		}
	}

	@Nested
	@DisplayName("기본 주문 생성 실패 케이스")
	class FailureCase {

		@Test
		@DisplayName("주문 생성 시 잘못 된 유저 아이디로 실패한다.")
		void createOrder_failure() {
			// given
			OrderCommand.OrderProduct orderProduct1 = OrderCommand.OrderProduct.of(1L, 3);
			OrderCommand.OrderProduct orderProduct2 = OrderCommand.OrderProduct.of(2L, 2);
			List<OrderCommand.OrderProduct> orderProducts = Arrays.asList(orderProduct1, orderProduct2);
			OrderCommand.Order command = OrderCommand.Order.of(999L, 0L, orderProducts);

			// when then
			assertThatThrownBy(() -> createOrderUseCase.execute(command))
				.isInstanceOf(RestApiException.class)
				.hasMessageContaining("User is inactive");

		}
	}
}