package kr.hhplus.be.server.config.jpa.order.infrastructure.orderproduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;

public interface JpaOrderProductRepository extends JpaRepository<OrderProduct, Long> {
	List<OrderProduct> findAllByOrderId(Long orderId);
}