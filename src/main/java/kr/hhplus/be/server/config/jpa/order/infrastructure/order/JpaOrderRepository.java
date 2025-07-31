package kr.hhplus.be.server.config.jpa.order.infrastructure.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

}
