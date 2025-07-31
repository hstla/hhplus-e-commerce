package kr.hhplus.be.server.config.jpa.product.repository;

import java.util.List;

import kr.hhplus.be.server.config.jpa.product.model.ProductOption;

public interface ProductOptionRepository {
	ProductOption save(ProductOption productOption);
	List<ProductOption> findAllByProductId(Long productIds);
	ProductOption findById(Long productOptionId);
}