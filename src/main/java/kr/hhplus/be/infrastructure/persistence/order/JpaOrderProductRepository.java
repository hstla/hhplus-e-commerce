package kr.hhplus.be.infrastructure.persistence.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.order.model.OrderProduct;

public interface JpaOrderProductRepository extends JpaRepository<OrderProduct, Long> {
	List<OrderProduct> findAllByOrderId(Long orderId);
}