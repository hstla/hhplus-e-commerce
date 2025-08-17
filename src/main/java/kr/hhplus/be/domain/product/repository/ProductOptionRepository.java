package kr.hhplus.be.domain.product.repository;

import java.util.List;

import kr.hhplus.be.domain.product.model.ProductOption;

public interface ProductOptionRepository {
	List<ProductOption> findAllByProductId(Long productId);
	ProductOption findById(Long productOptionId);
	ProductOption save(ProductOption option);
}