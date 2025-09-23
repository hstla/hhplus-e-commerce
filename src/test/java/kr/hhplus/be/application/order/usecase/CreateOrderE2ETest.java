package kr.hhplus.be.application.order.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.application.order.dto.OrderCommand;
import kr.hhplus.be.config.ConcurrentTestSupport;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderStatus;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.model.UserCouponStatus;
import kr.hhplus.be.infrastructure.persistence.coupon.JpaCouponRepository;
import kr.hhplus.be.infrastructure.persistence.order.JpaOrderRepository;
import kr.hhplus.be.infrastructure.persistence.product.JpaProductOptionRepository;
import kr.hhplus.be.infrastructure.persistence.product.JpaProductRepository;
import kr.hhplus.be.infrastructure.persistence.user.JpaUserRepository;
import kr.hhplus.be.infrastructure.persistence.usercoupon.JpaUserCouponRepository;

@DisplayName("주문 생성 Event Listener 테스트")
public class CreateOrderE2ETest extends ConcurrentTestSupport {

	@Autowired
	private CreateOrderUseCase orderUseCase;
	@Autowired
	private JpaOrderRepository orderRepository;
	@Autowired
	private JpaUserRepository userRepository;
	@Autowired
	private JpaUserCouponRepository userCouponRepository;
	@Autowired
	private JpaCouponRepository couponRepository;
	@Autowired
	private JpaProductRepository productRepository;
	@Autowired
	private JpaProductOptionRepository productOptionRepository;

	private User testUser;
	private UserCoupon testCoupon;
	private Product testProduct;
	private ProductOption testProductOption1;
	private ProductOption testProductOption2;

	@BeforeEach
	void setUp() {
		orderRepository.deleteAll();
		couponRepository.deleteAll();
		userRepository.deleteAll();
		userCouponRepository.deleteAll();
		productRepository.deleteAll();
		productOptionRepository.deleteAll();

		Coupon coupon = couponRepository.save(Coupon.create("테스트 쿠폰", CouponType.FIXED, 1_000L, 100, LocalDateTime.now().plusDays(3)));

		testUser = User.create("testuser", "test@email.com", "password");
		testUser.chargePoint(999_999L);
		userRepository.save(testUser);

		testCoupon = UserCoupon.publish(testUser.getId(), coupon.getId(), LocalDateTime.now().minusDays(1));
		userCouponRepository.save(testCoupon);
		createProduct();
	}

	private void createProduct() {
		testProduct = productRepository.save(Product.create("테스트 상품", ProductCategory.CLOTHING, "설명설명설명설명설명"));
		testProductOption1 = productOptionRepository.save(ProductOption.create(testProduct.getId(), "테스트 상품 옵션1", 1_000L, 100));
		testProductOption2 = productOptionRepository.save(ProductOption.create(testProduct.getId(), "테스트 상품 옵션2", 2_000L, 200));
	}

	@Nested
	@DisplayName("정상 주문 케이스")
	class SuccessCase {

		@Test
		@DisplayName("일반 주문 정상 처리")
		void normalOrderSuccess() {
			// given
			var orderProduct1 = OrderCommand.OrderProduct.of(testProductOption1.getId(), 3);
			var orderProduct2 = OrderCommand.OrderProduct.of(testProductOption2.getId(), 3);
			OrderCommand.Order command = OrderCommand.Order.of(testUser.getId(), null, List.of(orderProduct1, orderProduct2));

			// when
			orderUseCase.execute(command);

			// then
			await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
				List<Order> orders = orderRepository.findByUserId(testUser.getId());
				assertThat(orders).hasSize(1);

				Order order = orders.get(0);
				assertSoftly(soft -> {
					soft.assertThat(order.getUserId()).isEqualTo(testUser.getId());
					soft.assertThat(order.getUserCouponId()).isNull();
					soft.assertThat(order.getCreatedAt()).isNotNull();
					soft.assertThat(order.getStatus()).isEqualTo(OrderStatus.AWAITING_PAYMENT);
				});
			});
		}

		@Test
		@DisplayName("쿠폰 사용 주문 정상 처리")
		void couponOrderSuccess() {
			// given
			var orderProduct1 = OrderCommand.OrderProduct.of(testProductOption1.getId(), 3);
			var orderProduct2 = OrderCommand.OrderProduct.of(testProductOption2.getId(), 3);
			OrderCommand.Order command = OrderCommand.Order.of(testUser.getId(), testCoupon.getId(), List.of(orderProduct1, orderProduct2));

			// when
			orderUseCase.execute(command);

			// then
			await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
				List<Order> orders = orderRepository.findByUserId(testUser.getId());
				assertThat(orders).hasSize(1);

				Order order = orders.get(0);
				assertSoftly(soft -> {
					soft.assertThat(order.getUserId()).isEqualTo(testUser.getId());
					soft.assertThat(order.getUserCouponId()).isEqualTo(testCoupon.getId());
					soft.assertThat(order.getCreatedAt()).isNotNull();
					soft.assertThat(order.getStatus()).isEqualTo(OrderStatus.AWAITING_PAYMENT);

					UserCoupon coupon = userCouponRepository.findById(testCoupon.getId()).orElseThrow();
					soft.assertThat(coupon.getStatus()).isEqualTo(UserCouponStatus.USED);
				});
			});
		}
	}

	@Nested
	@DisplayName("실패 주문 케이스")
	class FailureCase {

		@Test
		@DisplayName("이미 사용된 쿠폰으로 주문 시 실패")
		void usedCouponOrderFail() {
			// given
			testCoupon.use(LocalDateTime.now());
			userCouponRepository.save(testCoupon);

			var orderProduct1 = OrderCommand.OrderProduct.of(testProductOption1.getId(), 3);
			var orderProduct2 = OrderCommand.OrderProduct.of(testProductOption2.getId(), 3);
			var command = OrderCommand.Order.of(testUser.getId(), testCoupon.getId(), List.of(orderProduct1, orderProduct2));

			// when
			orderUseCase.execute(command);

			// then
			await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
				List<Order> orders = orderRepository.findByUserId(testUser.getId());
				assertThat(orders).hasSize(1);

				Order order = orders.get(0);
				assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
			});
		}
	}

	@Nested
	@DisplayName("동시성 주문 케이스")
	class ConcurrencyCase {

		@Test
		@DisplayName("마지막 재고 1개 남은 경우, 여러 사용자가 주문 경쟁 시 한 명만 성공 (AWAITING_PAYMENT)")
		void lastStockConcurrency() throws InterruptedException {
			// given
			int numberOfThreads = 3;

			// 테스트 상품 옵션 재고를 1개로 설정
			testProductOption1 = productOptionRepository.save(
				ProductOption.create(testProduct.getId(), "테스트 상품 옵션1", 1_000L, 1) // 재고 1개
			);

			// when
			runConcurrentTestWithIndex(numberOfThreads, index -> {
				User user = userRepository.save(
					User.create("user-" + index, "test@test.com" + index, "password" + index)
				);

				var orderProduct = OrderCommand.OrderProduct.of(testProductOption1.getId(), 1);
				OrderCommand.Order command = OrderCommand.Order.of(user.getId(), null, List.of(orderProduct));

				orderUseCase.execute(command);
				return null;
			});

			// then
			await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
				List<Order> orders = orderRepository.findAll();

				assertSoftly(soft -> {
					soft.assertThat(orders).hasSize(numberOfThreads);

					long awaitingPaymentCount = orders.stream()
						.filter(order -> order.getStatus() == OrderStatus.AWAITING_PAYMENT)
						.count();
					soft.assertThat(awaitingPaymentCount).isEqualTo(1);

					long failedCount = orders.stream()
						.filter(order -> order.getStatus() == OrderStatus.FAILED)
						.count();
					soft.assertThat(failedCount).isEqualTo(2);
				});
			});
		}
	}
}