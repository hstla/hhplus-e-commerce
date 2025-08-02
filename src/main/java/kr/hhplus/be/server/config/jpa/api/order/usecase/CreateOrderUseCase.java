package kr.hhplus.be.server.config.jpa.api.order.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderResult;
import kr.hhplus.be.server.config.jpa.coupon.service.CouponService;
import kr.hhplus.be.server.config.jpa.order.service.OrderInfo;
import kr.hhplus.be.server.config.jpa.order.service.OrderService;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.product.service.ProductService;
import kr.hhplus.be.server.config.jpa.user.domain.component.UserValidator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

	private final UserValidator userValidator;
	private final CouponService couponService;
	private final ProductService productService;
	private final OrderService orderService;

	@Transactional
	public OrderResult.Order execute(OrderCommand.Order command) {
		LocalDateTime now = LocalDateTime.now();
		userValidator.validateExistingUser(command.getUserId());

		List<ProductOption> options = productService.orderProductOptions(command.getOrderItemRequests().stream().map(
			OrderCommand.OrderProduct::toInput).toList());

		OrderInfo.PreOrderInfo preOrderInfo = orderService.prepareOrderItems(options, command.getOrderItemRequests());

		long discountAmount = calculateDiscount(command.getUserCouponId(), preOrderInfo.getTotalAmount());

		OrderInfo.Info saveOrder = orderService.createOrder(command.getUserId(),
			command.getUserCouponId(), preOrderInfo, discountAmount, now);

		return OrderResult.Order.of(saveOrder);
	}

	private long calculateDiscount(Long couponId, long totalAmount) {
		if (couponId == null) return 0L;
		return couponService.useUserCoupon(couponId, totalAmount, LocalDateTime.now());
	}
}