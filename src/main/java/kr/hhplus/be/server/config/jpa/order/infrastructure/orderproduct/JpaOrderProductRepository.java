package kr.hhplus.be.server.config.jpa.order.infrastructure.orderproduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderProductRepository extends JpaRepository<OrderProductEntity, Long> {

	List<OrderProductEntity> findAllByOrderId(Long orderId);
}
