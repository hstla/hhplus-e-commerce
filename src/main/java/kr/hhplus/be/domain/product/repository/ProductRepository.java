package kr.hhplus.be.domain.product.repository;

import kr.hhplus.be.domain.product.model.Product;

public interface ProductRepository {
	Product findById(Long productId);
	Product save(Product product);
}