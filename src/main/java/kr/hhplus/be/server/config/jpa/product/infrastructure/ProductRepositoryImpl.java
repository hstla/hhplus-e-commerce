package kr.hhplus.be.server.config.jpa.product.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.product.domain.Product;
import kr.hhplus.be.server.config.jpa.product.domain.ProductRepository;

@Component
public class ProductRepositoryImpl implements ProductRepository {

	@Override
	public Optional<Product> findById(Long productId) {
		return Optional.empty();
	}

	@Override
	public Product save(Product product) {
		return null;
	}

	@Override
	public void deleteById(Long productId) {

	}

	@Override
	public void clear() {

	}
}
