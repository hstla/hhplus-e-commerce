package kr.hhplus.be.server.config.jpa.product.domain;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository {
	boolean existsById(Long productOptionId);
	List<ProductOption> findAllById(List<Long> productOptionIds);
	Optional<ProductOption> findById(Long productOptionId);
}
