package kr.hhplus.be.application.product.usecase;

import kr.hhplus.be.domain.product.model.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ProductRank2 {
	private Long productId;
	private String productName;
	private String productDescription;
	private ProductCategory productCategory;
	private Long totalSold;
}
