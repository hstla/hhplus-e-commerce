package kr.hhplus.be.server.config.jpa.product.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductCoreRepository implements ProductRepository {

	private final JpaProductRepository jpaProductRepository;

	@Override
	public Optional<Product> findById(Long productId) {
		return Optional.empty();
	}

	@Override
	public Product save(Product product) {
		return null;
	}

}
