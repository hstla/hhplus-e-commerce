package kr.hhplus.be.infrastructure.persistence.product;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.product.model.Product;

public interface JpaProductRepository extends JpaRepository<Product, Long> {

}
