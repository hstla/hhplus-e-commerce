package kr.hhplus.be.server.config.jpa.product.infrastructure.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

}
