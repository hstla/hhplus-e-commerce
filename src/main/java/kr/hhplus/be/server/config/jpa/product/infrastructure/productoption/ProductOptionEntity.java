package kr.hhplus.be.server.config.jpa.product.infrastructure.productoption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_option")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductOptionEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_option_id", unique = true, nullable = false)
	private Long id;

	@JoinColumn(name = "product_id", nullable = false)
	private Long productId;

	@Column(nullable = false, length = 255)
	private String optionName;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private int stock;

	public ProductOptionEntity(Long productId, String optionName, Long price, int stock) {
		this.productId = productId;
		this.optionName = optionName;
		this.price = price;
		this.stock = stock;
	}
}