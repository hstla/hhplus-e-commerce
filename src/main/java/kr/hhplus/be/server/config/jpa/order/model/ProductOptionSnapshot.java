package kr.hhplus.be.server.config.jpa.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProductOptionSnapshot {
	@Column(name = "product_name", nullable = false, length = 30)
	private String name;
	@Column(name = "product_stock", nullable = false)
	private int stock;
	@Column(name = "product_price", nullable = false)
	private Long price;

	public static ProductOptionSnapshot create(String name, int stock, Long price) {
		return new ProductOptionSnapshot(name, stock, price);
	}

	public long calculateOriginPrice() {
		return price * stock;
	}
}