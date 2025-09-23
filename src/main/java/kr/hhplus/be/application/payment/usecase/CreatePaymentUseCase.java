package kr.hhplus.be.application.payment.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.application.payment.dto.PaymentResult;
import kr.hhplus.be.application.payment.service.UserPointLockManager;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.payment.model.Payment;
import kr.hhplus.be.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.domain.payment.service.dto.PaymentInfo;
import kr.hhplus.be.global.error.OrderErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreatePaymentUseCase {

	private final UserPointLockManager userService;
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;

	@Transactional
	public PaymentResult.Pay execute(PaymentCommand.Pay command) {
		Order findOrder = orderRepository.findById(command.orderId());

		if (!findOrder.validatePayable()) {
			throw new RestApiException(OrderErrorCode.CANNOT_PAY_ORDER);
		}

		userService.deductPointWithLock(command.userId(), findOrder.getTotalPrice());

		Payment payment = Payment.create(findOrder.getId(), findOrder.getTotalPrice());
		Payment savePayment = paymentRepository.save(payment);

		findOrder.markAsCompleted();
		orderRepository.save(findOrder);

		return PaymentResult.Pay.of(PaymentInfo.Info.of(savePayment));
	}
}