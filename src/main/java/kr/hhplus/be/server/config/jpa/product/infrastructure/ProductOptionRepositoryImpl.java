package kr.hhplus.be.server.config.jpa.product.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.product.domain.ProductOption;
import kr.hhplus.be.server.config.jpa.product.domain.ProductOptionRepository;

@Component
public class ProductOptionRepositoryImpl  implements ProductOptionRepository {

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
