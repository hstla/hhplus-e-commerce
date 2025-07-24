package kr.hhplus.be.server.config.jpa.order.domain;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponInfo;
import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponType;
import kr.hhplus.be.server.config.jpa.error.OrderErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.order.domain.discount.DiscountPolicy;
import kr.hhplus.be.server.config.jpa.product.domain.ProductInfo;
import kr.hhplus.be.server.config.jpa.user.domain.UserInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final Map<CouponType, DiscountPolicy> discountPolicies;



	public OrderInfo.Info order(UserInfo.Info userInfo, List<ProductInfo.OptionInfo> productOptions, List<OrderInput.OrderProduct> oderProducts, CouponInfo.Info useCoupon, Long userCouponId) {
		Map<Long, ProductInfo.OptionInfo> optionInfoMap = productOptions.stream()
			.collect(Collectors.toMap(ProductInfo.OptionInfo::getProductOptionId, Function.identity()));

		long totalPrice = 0;
		for (OrderInput.OrderProduct orderProduct : oderProducts) {
			ProductInfo.OptionInfo option = optionInfoMap.get(orderProduct.getProductOptionId());
			long itemTotal = option.getPrice() * orderProduct.getQuantity();
			totalPrice += itemTotal;
		}

		long discountAmount = 0;
		if (useCoupon != null) {
			CouponType couponType = useCoupon.getDiscountType();
			DiscountPolicy discountPolicy = discountPolicies.get(couponType);
			discountAmount = discountPolicy.calculateDiscount(totalPrice, useCoupon.getDiscountValue());
		}
		long finalTotalPrice = Math.max(totalPrice - discountAmount, 0);

		Order order = Order.createOrder(userInfo.getId(), userCouponId, finalTotalPrice);
		return OrderInfo.Info.of(order);
	}

	public OrderInfo.Info payOrder(Long orderId) {
		Order order = getOrder(orderId);
		order.payValidate();
		return OrderInfo.Info.of(order);
	}

	private Order getOrder(Long orderId) {
		return orderRepository.findById(orderId).orElseThrow(() -> new RestApiException(OrderErrorCode.INACTIVE_ORDER));
	}

	public List<OrderProductInfo.Info> findOrderProducts(Long orderId) {
		List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);
		if (orderProducts.isEmpty()) {
			throw new RestApiException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND);
		}

		return orderProducts.stream()
			.map(OrderProductInfo.Info::of)
			.collect(Collectors.toList());
	}

	public void markAsPaid(Long orderId) {
		Order order = getOrder(orderId);
		order.markAsPaid();
	}
}