package kr.hhplus.be.server.config.jpa.product.infrastructure.product;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.product.infrastructure.mapper.ProductMapper;
import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductCoreRepository implements ProductRepository {

	private final JpaProductRepository jpaProductRepository;
	private final ProductMapper productMapper;

	@Override
	public Product findById(Long productId) {
		ProductEntity productEntity = jpaProductRepository.findById(productId).orElseThrow(() ->
			new RestApiException(ProductErrorCode.INACTIVE_PRODUCT));
		return productMapper.toModel(productEntity);
	}

	@Override
	public Product save(Product product) {
		ProductEntity save = jpaProductRepository.save(productMapper.toEntity(product));
		return productMapper.toModel(save);
	}
}