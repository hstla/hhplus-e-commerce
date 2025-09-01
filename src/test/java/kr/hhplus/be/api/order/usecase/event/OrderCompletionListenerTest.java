package kr.hhplus.be.api.order.usecase.event;

import kr.hhplus.be.domain.shared.event.CouponUsedEvent;
import kr.hhplus.be.domain.shared.event.dto.PricedOrderItemInfo;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderProductRepository;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderProduct;
import kr.hhplus.be.domain.order.model.OrderStatus;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("OrderCompletionListener 통합 테스트")
class OrderCompletionListenerTest extends IntegrationTestConfig {

	@Autowired
	private OrderCompletionListener orderCompletionListener;

	@Autowired
	private JpaUserRepository userRepository;

	@Autowired
	private JpaOrderRepository orderRepository;

	@Autowired
	private JpaOrderProductRepository orderProductRepository;

	private User testUser;
	private Order testOrder;
	private List<PricedOrderItemInfo> testOrderItems;
	private LocalDateTime now = LocalDateTime.now();

	@BeforeEach
	void setUp() {
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		userRepository.deleteAll();

		testUser = User.create("tester", "tester@email.com", "password");
		testUser = userRepository.save(testUser);

		testOrder = Order.createPending(testUser.getId(), null, now);
		orderRepository.save(testOrder);

		testOrderItems = List.of(
			new PricedOrderItemInfo(1L, 101L, 2, 10000L, "상품1"),
			new PricedOrderItemInfo(2L, 102L, 1, 15000L, "상품2")
		);
	}

	@Nested
	@DisplayName("정상 케이스")
	class SuccessCase {

		@Test
		@DisplayName("쿠폰 사용 완료 시 주문 상태가 결제 대기로 변경되어야 한다")
		void changeOrderStatus() {
			// given
			long totalOriginalPrice = 35000L;
			long discountPrice = 5000L;
			CouponUsedEvent event = new CouponUsedEvent(testOrder.getId(), totalOriginalPrice, discountPrice, testOrderItems);

			// when
			orderCompletionListener.orderCompleteEvent(event);

			// then
			Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();

			assertSoftly(softly -> {
				softly.assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.AWAITING_PAYMENT);
				softly.assertThat(updatedOrder.getOriginalPrice()).isEqualTo(totalOriginalPrice);
				softly.assertThat(updatedOrder.getDiscountPrice()).isEqualTo(discountPrice);
				softly.assertThat(updatedOrder.getTotalPrice()).isEqualTo(30000L); // 35000 - 5000
			});
		}

		@Test
		@DisplayName("쿠폰 미사용 시에도 주문 상태가 결제 대기로 변경되어야 한다")
		void noCouponChangeOrderStatus() {
			// given
			long totalOriginalPrice = 35000L;
			long discountPrice = 0L;
			CouponUsedEvent event = new CouponUsedEvent(testOrder.getId(), totalOriginalPrice, discountPrice, testOrderItems);

			// when
			orderCompletionListener.orderCompleteEvent(event);

			// then
			Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();

			assertSoftly(softly -> {
				softly.assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.AWAITING_PAYMENT);
				softly.assertThat(updatedOrder.getOriginalPrice()).isEqualTo(totalOriginalPrice);
				softly.assertThat(updatedOrder.getDiscountPrice()).isEqualTo(0L);
				softly.assertThat(updatedOrder.getTotalPrice()).isEqualTo(35000L);
			});
		}

		@Test
		@DisplayName("주문 상품들이 정상적으로 생성되어야 한다")
		void createOrderProducts() {
			// given
			long totalOriginalPrice = 35000L;
			long discountPrice = 5000L;
			CouponUsedEvent event = new CouponUsedEvent(testOrder.getId(), totalOriginalPrice, discountPrice, testOrderItems);

			// when
			orderCompletionListener.orderCompleteEvent(event);

			// then
			List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(testOrder.getId());

			assertSoftly(softly -> {
				softly.assertThat(orderProducts).hasSize(2);

				OrderProduct product1 = orderProducts.stream()
					.filter(op -> op.getProductOptionVO().getProductOptionId().equals(1L))
					.findFirst().orElseThrow();
				softly.assertThat(product1.getProductOptionVO().getName()).isEqualTo("상품1");
				softly.assertThat(product1.getProductOptionVO().getStock()).isEqualTo(2);
				softly.assertThat(product1.getProductOptionVO().getPrice()).isEqualTo(10000L);

				OrderProduct product2 = orderProducts.stream()
					.filter(op -> op.getProductOptionVO().getProductOptionId().equals(2L))
					.findFirst().orElseThrow();
				softly.assertThat(product2.getProductOptionVO().getName()).isEqualTo("상품2");
				softly.assertThat(product2.getProductOptionVO().getStock()).isEqualTo(1);
				softly.assertThat(product2.getProductOptionVO().getPrice()).isEqualTo(15000L);
			});
		}

		@Test
		@DisplayName("총 가격이 정확히 계산되어야 한다")
		void calculateTotalPrice() {
			// given
			long totalOriginalPrice = 50000L;
			long discountPrice = 10000L;
			CouponUsedEvent event = new CouponUsedEvent(testOrder.getId(), totalOriginalPrice, discountPrice, testOrderItems);

			// when
			orderCompletionListener.orderCompleteEvent(event);

			// then
			Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();

			assertSoftly(softly -> {
				softly.assertThat(updatedOrder.getOriginalPrice()).isEqualTo(50000L);
				softly.assertThat(updatedOrder.getDiscountPrice()).isEqualTo(10000L);
				softly.assertThat(updatedOrder.getTotalPrice()).isEqualTo(40000L);
			});
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class FailureCase {

		@Test
		@DisplayName("존재하지 않는 주문ID로 요청 시, 예외가 발생해야 한다")
		void orderNotExists() {
			// given
			long nonExistingOrderId = 9999L;
			CouponUsedEvent event = new CouponUsedEvent(nonExistingOrderId, 35000L, 5000L, testOrderItems);

			// when & then
			assertThatThrownBy(() -> orderCompletionListener.orderCompleteEvent(event))
				.hasMessageContaining("Order is inactive")
				.isInstanceOf(Exception.class);
 		}
	}
}