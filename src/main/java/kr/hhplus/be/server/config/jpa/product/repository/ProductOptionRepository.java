package kr.hhplus.be.server.config.jpa.product.repository;

import java.util.List;

import kr.hhplus.be.server.config.jpa.product.model.ProductOption;

public interface ProductOptionRepository {
	List<ProductOption> findAllByProductId(Long productId);
	ProductOption findById(Long productOptionId);
	ProductOption save(ProductOption option);
}