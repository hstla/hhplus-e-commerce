package kr.hhplus.be.server.config.jpa.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import kr.hhplus.be.server.config.jpa.order.repository.OrderProductRepository;
import kr.hhplus.be.server.config.jpa.order.repository.OrderRepository;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderProductRepository orderProductRepository;

	@InjectMocks
	private OrderService orderService;

	@Nested
	@DisplayName("prepareOrderItems 메서드는")
	class PrepareOrderItemsTest {

		@Test
		@DisplayName("상품 옵션과 주문 요청으로 주문 상품 리스트와 총 금액을 반환한다")
		void prepareOrderItems_success() {
			// given
			ProductOption option1 = new ProductOption(1L, 100L, "옵션1", 1000L, 5);
			ProductOption option2 = new ProductOption(2L, 100L, "옵션2", 2000L, 10);

			List<ProductOption> options = List.of(option1, option2);
			List<OrderCommand.OrderProduct> orderRequests = List.of(
				OrderCommand.OrderProduct.of(1L, 2),
				OrderCommand.OrderProduct.of(2L, 3)
			);

			// when
			var preOrderInfo = orderService.prepareOrderItems(options, orderRequests);

			// then
			assertThat(preOrderInfo).isNotNull();
			assertThat(preOrderInfo.getTotalAmount()).isEqualTo(2*1000L + 3*2000L);

			List<OrderProduct> orderProducts = preOrderInfo.getOrderProducts();
			assertThat(orderProducts).hasSize(2);
			assertThat(orderProducts).extracting(OrderProduct::getProductOptionId)
				.containsExactlyInAnyOrder(1L, 2L);
			assertThat(orderProducts).extracting(OrderProduct::getQuantity)
				.containsExactlyInAnyOrder(2, 3);
		}
	}

	@Nested
	@DisplayName("createOrder 메서드는")
	class CreateOrderTest {

		@Test
		@DisplayName("주문을 저장하고 주문 상품에 주문 ID를 설정하여 저장한다")
		void createOrder_success() {
			// given
			long userId = 1L;
			Long userCouponId = null;
			long discountAmount = 500L;
			LocalDateTime now = LocalDateTime.now();

			OrderProduct op1 = OrderProduct.create(null, 1L, 2, 1000L);
			OrderProduct op2 = OrderProduct.create(null, 2L, 1, 2000L);
			List<OrderProduct> orderProducts = List.of(op1, op2);
			OrderInfo.PreOrderInfo preOrderInfo = OrderInfo.PreOrderInfo.of(4000L, orderProducts);

			Order savedOrder = new Order(10L, userId, userCouponId, 3500L, OrderStatus.CREATED, now);

			given(orderRepository.save(any(Order.class))).willReturn(savedOrder);
			given(orderProductRepository.save(any(OrderProduct.class))).willAnswer(invocation -> invocation.getArgument(0));

			// when
			var result = orderService.createOrder(userId, userCouponId, preOrderInfo, discountAmount, now);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(10L);

			ArgumentCaptor<OrderProduct> captor = ArgumentCaptor.forClass(OrderProduct.class);
			then(orderProductRepository).should(times(orderProducts.size())).save(captor.capture());

			List<OrderProduct> savedOrderProducts = captor.getAllValues();
			for (OrderProduct op : savedOrderProducts) {
				assertThat(op.getOrderId()).isEqualTo(10L);
			}
		}
	}

	@Nested
	@DisplayName("payComplete 메서드는")
	class PayCompleteTest {

		@Test
		@DisplayName("주문 상태를 결제 완료로 변경하고 저장한다")
		void payComplete_success() {
			// given
			Long orderId = 100L;
			Order existingOrder = new Order(orderId,1L, null, 5000L, OrderStatus.CREATED, LocalDateTime.now());

			given(orderRepository.findById(orderId)).willReturn(existingOrder);
			given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

			// when
			var result = orderService.payComplete(orderId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(orderId);
			assertThat(existingOrder.getStatus()).isEqualTo(OrderStatus.PAID);

			then(orderRepository).should(times(1)).findById(orderId);
			then(orderRepository).should(times(1)).save(existingOrder);
		}
	}
}
