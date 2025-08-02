package kr.hhplus.be.server.config.jpa.product.infrastructure.productoption;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.product.infrastructure.mapper.ProductMapper;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductOptionCoreRepository implements ProductOptionRepository {

	private final JpaProductOptionRepository jpaProductOptionRepository;
	private final ProductMapper productMapper;

	@Override
	public ProductOption findById(Long productOptionId) {
		ProductOptionEntity productOptionEntity = jpaProductOptionRepository.findById(productOptionId).orElseThrow(() ->
			new RestApiException(ProductErrorCode.NOT_FOUND_PRODUCT_OPTION));
		return productMapper.toModel(productOptionEntity);
	}

	@Override
	public ProductOption save(ProductOption productOption) {
		ProductOptionEntity save = jpaProductOptionRepository.save(productMapper.toEntity(productOption));
		return productMapper.toModel(save);
	}

	@Override
	public List<ProductOption> findAllByProductId(Long productId) {
		List<ProductOptionEntity> optionEntities = jpaProductOptionRepository.findAllByProductId(productId);
		return optionEntities.stream().map(productMapper::toModel).toList();
	}
}
