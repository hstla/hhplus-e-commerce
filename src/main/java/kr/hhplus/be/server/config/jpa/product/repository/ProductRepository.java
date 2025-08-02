package kr.hhplus.be.server.config.jpa.product.repository;

import kr.hhplus.be.server.config.jpa.product.model.Product;

public interface ProductRepository {
	Product findById(Long productId);
	Product save(Product product);
}