package kr.hhplus.be.server.config.jpa.product.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductOptionCoreRepository implements ProductOptionRepository {

	private final JpaProductOptionRepository jpaProductOptionRepository;

	@Override
	public List<ProductOption> findAllByProductId(Long productId) {
		return jpaProductOptionRepository.findAllByProductId(productId);
	}

	@Override
	public ProductOption findById(Long productOptionId) {
		return jpaProductOptionRepository.findById(productOptionId)
			.orElseThrow(() -> new RestApiException(ProductErrorCode.NOT_FOUND_PRODUCT_OPTION));
	}

	@Override
	public ProductOption save(ProductOption option) {
		return jpaProductOptionRepository.save(option);
	}
}
