package kr.hhplus.be.server.config.jpa.product.infrastructure.productoption;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductOptionRepository extends JpaRepository<ProductOptionEntity, Long> {

	List<ProductOptionEntity> findAllByProductId(Long productId);
}
