package kr.hhplus.be.server.config.jpa.api.product.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.jpa.api.payment.usecase.CreatePaymentUseCase;
import kr.hhplus.be.server.config.jpa.api.payment.usecase.PaymentCommand;
import kr.hhplus.be.server.config.jpa.api.payment.usecase.PaymentResult;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderStatus;
import kr.hhplus.be.server.config.jpa.order.service.OrderInfo;
import kr.hhplus.be.server.config.jpa.order.service.OrderService;
import kr.hhplus.be.server.config.jpa.payment.model.Payment;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentStatus;
import kr.hhplus.be.server.config.jpa.payment.model.PaymentType;
import kr.hhplus.be.server.config.jpa.payment.service.PaymentInfo;
import kr.hhplus.be.server.config.jpa.payment.service.PaymentService;
import kr.hhplus.be.server.config.jpa.user.domain.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePaymentUseCase 단위 테스트")
class CreatePaymentUseCaseTest {

	@Mock
	private PaymentService paymentService;

	@Mock
	private OrderService orderService;

	@Mock
	private UserService userService;

	@InjectMocks
	private CreatePaymentUseCase createPaymentUseCase;

	@Nested
	@DisplayName("성공 케이스")
	class SuccessCases {

		@Test
		@DisplayName("pay 메서드는 주문 완료 처리, 포인트 결제, 결제 저장 후 결제 결과를 반환한다")
		void pay_success() {
			// given
			Long orderId = 1L;
			Long userId = 2L;
			long totalPrice = 5000L;
			Order order = new Order(orderId, userId, null, totalPrice, OrderStatus.CREATED, LocalDateTime.now());
			Payment payment = new Payment(1L, orderId, userId, totalPrice, PaymentStatus.PENDING);

			PaymentCommand.Pay command = PaymentCommand.Pay.of(orderId, userId, PaymentType.POINT);
			OrderInfo.Info orderInfo = OrderInfo.Info.of(order);
			PaymentInfo.Info paymentInfo = PaymentInfo.Info.of(payment);

			given(orderService.payComplete(orderId)).willReturn(orderInfo);
			willDoNothing().given(userService).pointPay(userId, totalPrice);
			given(paymentService.pay(orderId, userId, totalPrice)).willReturn(paymentInfo);

			// when
			PaymentResult.Pay result = createPaymentUseCase.pay(command);

			// then
			then(orderService).should(times(1)).payComplete(orderId);
			then(userService).should(times(1)).pointPay(userId, totalPrice);
			then(paymentService).should(times(1)).pay(orderId, userId, totalPrice);

			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(paymentInfo.getId());
			assertThat(result.getOrderId()).isEqualTo(paymentInfo.getOrderId());
			assertThat(result.getUserId()).isEqualTo(paymentInfo.getUserId());
			assertThat(result.getPaymentAmount()).isEqualTo(paymentInfo.getPaymentAmount());
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class FailureCases {

		@Test
		@DisplayName("주문 완료 처리 실패 시 예외를 던진다")
		void pay_orderPayCompleteFail() {
			// given
			Long orderId = 1L;
			Long userId = 2L;
			PaymentCommand.Pay command = PaymentCommand.Pay.of(orderId, userId, PaymentType.POINT);

			given(orderService.payComplete(orderId)).willThrow(new RuntimeException("Order payComplete failed"));

			// when & then
			assertThatThrownBy(() -> createPaymentUseCase.pay(command))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Order payComplete failed");

			then(orderService).should(times(1)).payComplete(orderId);
			then(userService).should(never()).pointPay(anyLong(), anyLong());
			then(paymentService).should(never()).pay(anyLong(), anyLong(), anyLong());
		}

		@Test
		@DisplayName("포인트 결제 실패 시 예외를 던진다")
		void pay_pointPayFail() {
			// given
			Long orderId = 1L;
			Long userId = 2L;
			long totalPrice = 5000L;
			Order order = new Order(orderId, userId, null, totalPrice, OrderStatus.CREATED, LocalDateTime.now());

			PaymentCommand.Pay command = PaymentCommand.Pay.of(orderId, userId, PaymentType.POINT);
			OrderInfo.Info orderInfo = OrderInfo.Info.of(order);

			given(orderService.payComplete(orderId)).willReturn(orderInfo);
			willThrow(new RuntimeException("Point payment failed")).given(userService).pointPay(userId, totalPrice);

			// when & then
			assertThatThrownBy(() -> createPaymentUseCase.pay(command))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Point payment failed");

			then(orderService).should(times(1)).payComplete(orderId);
			then(userService).should(times(1)).pointPay(userId, totalPrice);
			then(paymentService).should(never()).pay(anyLong(), anyLong(), anyLong());
		}

		@Test
		@DisplayName("결제 저장 실패 시 예외를 던진다")
		void pay_paymentSaveFail() {
			// given
			Long orderId = 1L;
			Long userId = 2L;
			long totalPrice = 5000L;
			Order order = new Order(orderId, userId, null, totalPrice, OrderStatus.CREATED, LocalDateTime.now());

			PaymentCommand.Pay command = PaymentCommand.Pay.of(orderId, userId, PaymentType.POINT);
			OrderInfo.Info orderInfo = OrderInfo.Info.of(order);

			given(orderService.payComplete(orderId)).willReturn(orderInfo);
			willDoNothing().given(userService).pointPay(userId, totalPrice);
			given(paymentService.pay(orderId, userId, totalPrice)).willThrow(new RuntimeException("Payment save failed"));

			// when & then
			assertThatThrownBy(() -> createPaymentUseCase.pay(command))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Payment save failed");

			then(orderService).should(times(1)).payComplete(orderId);
			then(userService).should(times(1)).pointPay(userId, totalPrice);
			then(paymentService).should(times(1)).pay(orderId, userId, totalPrice);
		}
	}
}