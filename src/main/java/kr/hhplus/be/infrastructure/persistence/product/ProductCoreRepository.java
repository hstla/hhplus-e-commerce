package kr.hhplus.be.infrastructure.persistence.product;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import kr.hhplus.be.global.error.ProductErrorCode;
import kr.hhplus.be.global.error.RestApiException;
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