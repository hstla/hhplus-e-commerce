package kr.hhplus.be.domain.product.model;

import lombok.Getter;

@Getter
public enum ProductCategory {
	CLOTHING("의류"),
	SHOES("신발"),
	FOOD("식품"),
	DIGITAL("디지털/가전"),
	BEAUTY("뷰티")
	;

	private final String description;

	ProductCategory(String description) {
		this.description = description;
	}
}
