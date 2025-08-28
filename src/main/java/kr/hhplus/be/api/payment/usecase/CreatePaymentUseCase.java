package kr.hhplus.be.api.payment.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.api.payment.usecase.helper.UserPointLockManager;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.payment.model.Payment;
import kr.hhplus.be.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.domain.payment.service.PaymentInfo;
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
		findOrder.markAsCompleted();
		Order savedOrder = orderRepository.save(findOrder);

		userService.deductPointWithLock(command.userId(), savedOrder.getTotalPrice());

		Payment payment = Payment.create(savedOrder.getId(), savedOrder.getTotalPrice());
		Payment savePayment = paymentRepository.save(payment);

		PaymentInfo.Info savedPaymentInfo = PaymentInfo.Info.of(savePayment);
		// todo 결제 정보 외부 api 전송
		return PaymentResult.Pay.of(savedPaymentInfo);
	}
}