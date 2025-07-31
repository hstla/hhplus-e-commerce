package kr.hhplus.be.server.config.jpa.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.order.usecase.dto.OrderCommand;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.repository.OrderProductRepository;
import kr.hhplus.be.server.config.jpa.order.repository.OrderRepository;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;

	@Transactional
	public OrderInfo.Info createOrder(long userId, Long userCouponId, OrderInfo.PreOrderInfo preOrderInfo, long discountAmount, LocalDateTime now) {
		long finalAmount = calculateFinalAmount(preOrderInfo.getTotalAmount(), discountAmount);
		Order savedOrder = Order.create(userId, userCouponId, finalAmount, now);
		Order save = orderRepository.save(savedOrder);

		List<OrderProduct> orderProducts = preOrderInfo.getOrderProducts();

		orderProducts.forEach((op) -> {
			op.setOrderId(save.getId());
			orderProductRepository.save(op);
		});

		return OrderInfo.Info.of(save);
	}

	public OrderInfo.Info payComplete(Long orderId) {
		Order findOrder = orderRepository.findById(orderId);
		findOrder.markAsPaid();

		Order savedOrder = orderRepository.save(findOrder);
		return OrderInfo.Info.of(savedOrder);
	}

	public OrderInfo.PreOrderInfo prepareOrderItems(List<ProductOption> options, List<OrderCommand.OrderProduct> orderItemRequests) {
		Map<Long, ProductOption> optionMap = options.stream()
			.collect(Collectors.toMap(ProductOption::getId, Function.identity()));

		// 주문 항목 매핑
		List<OrderProduct> orderItems = orderItemRequests.stream()
			.map(req -> {
				ProductOption option = optionMap.get(req.getProductOptionId());
				return OrderProduct.create(null, option.getId(), req.getQuantity(), option.getPrice());
			})
			.toList();

		long totalAmount = calculateTotalAmount(orderItems);

		return OrderInfo.PreOrderInfo.of(totalAmount, orderItems);
	}

	private long calculateFinalAmount(long totalAmount, long discountAmount) {
		return Math.max(totalAmount - discountAmount, 0);
	}

	private long calculateTotalAmount(List<OrderProduct> orderProducts) {
		return orderProducts.stream()
			.mapToLong(OrderProduct::getCalculateAmount)
			.sum();
	}
}