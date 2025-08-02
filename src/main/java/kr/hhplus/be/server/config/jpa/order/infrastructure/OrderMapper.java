package kr.hhplus.be.server.config.jpa.order.infrastructure;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.order.infrastructure.order.OrderEntity;
import kr.hhplus.be.server.config.jpa.order.infrastructure.orderproduct.OrderProductEntity;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderMapper {

	// order mapper
	public OrderEntity toEntity(Order order) {
		return new OrderEntity(
			order.getId(),
			order.getUserId(),
			order.getUserCouponId(),
			order.getTotalPrice(),
			order.getStatus(),
			order.getOrderAt()
		);
	}

	public Order toModel(OrderEntity orderEntity) {
		return new Order(
			orderEntity.getId(),
			orderEntity.getUserId(),
			orderEntity.getUserCouponId(),
			orderEntity.getTotalPrice(),
			orderEntity.getStatus(),
			orderEntity.getOrderAt()
		);
	}

	// orderProduct mapper
	public OrderProductEntity toEntity(OrderProduct orderProduct) {
		return new OrderProductEntity(
			orderProduct.getId(),
			orderProduct.getOrderId(),
			orderProduct.getProductOptionId(),
			orderProduct.getQuantity(),
			orderProduct.getUnitPrice()
		);
	}

	public OrderProduct toModel(OrderProductEntity orderProductEntity) {
		return new OrderProduct(
			orderProductEntity.getId(),
			orderProductEntity.getOrderId(),
			orderProductEntity.getProductOptionId(),
			orderProductEntity.getQuantity(),
			orderProductEntity.getUnitPrice()
		);
	}
}