package kr.hhplus.be.server.config.jpa.order.application;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponInfo;
import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponService;
import kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon.UserCouponService;
import kr.hhplus.be.server.config.jpa.order.domain.OrderInfo;
import kr.hhplus.be.server.config.jpa.order.domain.OrderInput;
import kr.hhplus.be.server.config.jpa.order.domain.OrderService;
import kr.hhplus.be.server.config.jpa.product.domain.ProductInfo;
import kr.hhplus.be.server.config.jpa.product.domain.ProductService;
import kr.hhplus.be.server.config.jpa.user.domain.UserInfo;
import kr.hhplus.be.server.config.jpa.user.domain.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderFacade {

	private final OrderService orderService;
	private final UserService userService;
	private final UserCouponService userCouponService;
	private final CouponService couponService;
	private final ProductService productService;

	public OrderResult.Order order(OrderCommand.Order command) {
		UserInfo.Info user = userService.findUser(command.getUserId());

		// 쿠폰 검증 및 조회
		CouponInfo.Info useCoupon = null;
		if (command.getUserCouponId() != null) {
			Long couponId = userCouponService.useUserCoupon(command.getUserId(), command.getUserCouponId());
			useCoupon = couponService.useCoupon(couponId);
		}

		// 상품 옵션 조회
		List<Long> productIds = command.getOrderItemRequests().stream()
			.map(OrderCommand.OrderProduct::getProductOptionId)
			.toList();

		// 상품정보찾기
		List<ProductInfo.OptionInfo> allProductOptions = productService.getOptionsByProductIds(productIds);
		Stream<OrderInput.OrderProduct> orderInputs = command.getOrderItemRequests()
			.stream()
			.map(OrderCommand.OrderProduct::toInput);

		OrderInfo.Info orderResultInfo = orderService.order(user, allProductOptions, orderInputs.toList(), useCoupon, command.getUserCouponId());
		return OrderResult.Order.of(orderResultInfo);
	}
}