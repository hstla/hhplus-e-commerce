package kr.hhplus.be.server.config.jpa.payment.application;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.order.domain.OrderInfo;
import kr.hhplus.be.server.config.jpa.order.domain.OrderProductInfo;
import kr.hhplus.be.server.config.jpa.order.domain.OrderService;
import kr.hhplus.be.server.config.jpa.payment.domain.PaymentInfo;
import kr.hhplus.be.server.config.jpa.payment.domain.PaymentService;
import kr.hhplus.be.server.config.jpa.product.domain.ProductService;
import kr.hhplus.be.server.config.jpa.user.domain.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

	private final PaymentService paymentService;
	private final OrderService orderService;
	private final ProductService productService;
	private final UserService userService;

	public PaymentResult.Pay pay(PaymentCommand.Pay command) {
		OrderInfo.Info order = orderService.payOrder(command.getOrderId());

		List<OrderProductInfo.Info> orderProducts = orderService.findOrderProducts(order.getId());
		productService.payProduct(orderProducts);
		userService.payUserPoint(order.getUserId(), order.getTotalPrice());

		orderService.markAsPaid(order.getId());

		PaymentInfo.Info pay = paymentService.pay(order.getId(), order.getUserId(), order.getTotalPrice());
		return PaymentResult.Pay.of(pay);
	}
}
