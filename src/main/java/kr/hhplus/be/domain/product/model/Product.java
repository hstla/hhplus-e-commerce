package kr.hhplus.be.domain.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.global.common.BaseEntity;
import kr.hhplus.be.global.error.ProductErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

	private static final int MIN_NAME_LENGTH = 1;
	private static final int MAX_NAME_LENGTH = 30;
	private static final int MIN_DESCRIPTION_LENGTH = 10;
	private static final int MAX_DESCRIPTION_LENGTH = 200;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	@Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
	private String name;
	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private ProductCategory category;
	@Column(name = "description", nullable = false, length = MAX_DESCRIPTION_LENGTH)
	private String description;

	public static Product create(String name, ProductCategory category, String description) {
		validateName(name);
		validateDescription(description);
		return new Product(null, name, category, description);
	}

	private static void validateName(String name) {
		if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
			throw new RestApiException(ProductErrorCode.INVALID_PRODUCT_NAME);
		}
	}

	private static void validateDescription(String description) {
		if (description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH) {
			throw new RestApiException(ProductErrorCode.INVALID_DESCRIPTION);
		}
	}
}