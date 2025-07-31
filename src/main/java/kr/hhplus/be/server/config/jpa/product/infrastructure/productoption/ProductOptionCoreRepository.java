package kr.hhplus.be.server.config.jpa.product.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductOptionCoreRepository implements ProductOptionRepository {

	private final JpaProductOptionRepository jpaProductOptionRepository;


	@Override
	public boolean existsById(Long productOptionId) {
		return false;
	}

	@Override
	public List<ProductOption> findAllById(List<Long> productOptionIds) {
		return List.of();
	}

	@Override
	public Optional<ProductOption> findById(Long productOptionId) {
		return Optional.empty();
	}
}
