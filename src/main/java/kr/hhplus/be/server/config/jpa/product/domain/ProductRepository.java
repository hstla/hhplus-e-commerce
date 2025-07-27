package kr.hhplus.be.server.config.jpa.product.domain;

import java.util.Optional;

public interface ProductRepository {
	Optional<Product> findById(Long productId);
	Product save(Product product);
	void deleteById(Long productId);
	void clear();
}