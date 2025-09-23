package kr.hhplus.be.infrastructure.persistence.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.product.model.ProductOption;

public interface JpaProductOptionRepository extends JpaRepository<ProductOption, Long> {
	List<ProductOption> findAllByProductId(Long productId);
}