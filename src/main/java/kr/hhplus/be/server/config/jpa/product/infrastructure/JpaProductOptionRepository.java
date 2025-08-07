package kr.hhplus.be.server.config.jpa.product.infrastructure.productoption;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.config.jpa.product.model.ProductOption;

public interface JpaProductOptionRepository extends JpaRepository<ProductOption, Long> {
	List<ProductOption> findAllByProductId(Long productId);
}