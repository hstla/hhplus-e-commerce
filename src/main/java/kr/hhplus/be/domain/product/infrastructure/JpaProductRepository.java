package kr.hhplus.be.domain.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.product.model.Product;

public interface JpaProductRepository extends JpaRepository<Product, Long> {

}
