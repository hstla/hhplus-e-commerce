package kr.hhplus.be.server.config.jpa.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductOption extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_option_id", unique = true)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	private String optionName;
	private Long price;
	private int stock;

	public ProductOption(Product product, String optionName, Long price, int stock) {
		this.product = product;
		this.optionName = optionName;
		this.price = price;
		this.stock = stock;
	}

	public static ProductOption createProductOption(Product product, String optionName, Long price, int stockQuantity) {
		return new ProductOption(product, optionName, price, stockQuantity);
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void decreaseStock(int quantity) {
		if (this.stock < quantity) {
			throw new RestApiException(ProductErrorCode.OUT_OF_STOCK);
		}
		this.stock -= quantity;
	}
}