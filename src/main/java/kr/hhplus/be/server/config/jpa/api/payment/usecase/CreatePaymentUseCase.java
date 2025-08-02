package kr.hhplus.be.server.config.jpa.api.payment.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.order.service.OrderInfo;
import kr.hhplus.be.server.config.jpa.order.service.OrderService;
import kr.hhplus.be.server.config.jpa.payment.service.PaymentInfo;
import kr.hhplus.be.server.config.jpa.payment.service.PaymentService;
import kr.hhplus.be.server.config.jpa.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreatePaymentUseCase {

	private final PaymentService paymentService;
	private final OrderService orderService;
	private final UserService userService;

	@Transactional
	public PaymentResult.Pay pay(PaymentCommand.Pay command) {
		OrderInfo.Info payOrder = orderService.payComplete(command.getOrderId());

		userService.pointPay(command.userId, payOrder.getTotalPrice());
		PaymentInfo.Info savedPayment = paymentService.pay(payOrder.getId(), command.getUserId(), payOrder.getTotalPrice());

		// todo 결제 정보 외부 api 전송
		return PaymentResult.Pay.of(savedPayment);
	}
}