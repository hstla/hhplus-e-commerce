package kr.hhplus.be.server.config.jpa.order.infrastructure.orderproduct;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_product") // 테이블명 명시
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderProductEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_product_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@Column(name = "product_option_id", nullable = false)
	private Long productOptionId;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private Long unitPrice;

	public OrderProductEntity(Long orderId, Long productOptionId, int quantity, Long unitPrice) {
		this.orderId = orderId;
		this.productOptionId = productOptionId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public void updateQuantity(int newQuantity) {
		this.quantity = newQuantity;
	}
}