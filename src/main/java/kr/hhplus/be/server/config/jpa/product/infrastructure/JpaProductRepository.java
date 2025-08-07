package kr.hhplus.be.server.config.jpa.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.config.jpa.product.model.Product;

public interface JpaProductRepository extends JpaRepository<Product, Long> {

}
