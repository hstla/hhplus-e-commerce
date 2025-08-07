package kr.hhplus.be.server.config.jpa.product.infrastructure.product;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductCoreRepository implements ProductRepository {

	private final JpaProductRepository jpaProductRepository;

	@Override
	public Product findById(Long productId) {
		return jpaProductRepository.findById(productId)
			.orElseThrow(() -> new RestApiException(ProductErrorCode.INACTIVE_PRODUCT));
	}

	@Override
	public Product save(Product product) {
		return jpaProductRepository.save(product);
	}
}