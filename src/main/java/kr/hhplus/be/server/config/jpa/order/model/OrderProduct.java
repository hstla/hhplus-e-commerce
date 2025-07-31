package kr.hhplus.be.server.config.jpa.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_product_id", unique = true)
	private Long id;
	private Long orderId;
	private Long productOptionId;
	private int quantity;
	private int unitPrice;

	public OrderProduct(Long orderId, Long productOptionId, int quantity, int unitPrice) {
		this.orderId = orderId;
		this.productOptionId = productOptionId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public static OrderProduct CreateOrderProduct(Long orderId, Long productOptionId, int quantity, int unitPrice) {
		return new OrderProduct(orderId, productOptionId, quantity, unitPrice);
	}
}