package kr.hhplus.be.server.config.jpa.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductOption extends BaseEntity {
	private static final int MIN_NAME_LENGTH = 1;
	private static final int MAX_NAME_LENGTH = 30;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@JoinColumn(name = "product_id", nullable = false)
	private Long productId;
	@Version
	@Column(name = "version")
	private Long version;
	@Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
	private String name;
	@Column(name = "price", nullable = false)
	private Long price;
	@Column(name = "stock", nullable = false)
	private int stock;

	public static ProductOption create(Long productId, String optionName, Long price, int stockQuantity) {
		validateName(optionName);
		return new ProductOption(null, productId, 0L, optionName, price, stockQuantity);
	}

	private static void validateName(String name) {
		if (name.length() < MIN_NAME_LENGTH ||  name.length() > MAX_NAME_LENGTH) {
			throw new RestApiException(ProductErrorCode.INVALID_OPTION_NAME);
		}
	}

	public void orderDecreaseStock(int quantity) {
		validateOrderStock(quantity);
		this.stock -= quantity;
	}

	private void validateOrderStock(int quantity) {
		if (this.stock < quantity) {
			throw new RestApiException(ProductErrorCode.OUT_OF_STOCK);
		}
	}
}