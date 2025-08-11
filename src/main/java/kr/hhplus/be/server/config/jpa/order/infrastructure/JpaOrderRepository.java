package kr.hhplus.be.server.config.jpa.order.infrastructure;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.config.jpa.order.model.Order;

public interface JpaOrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUserId(long userId);
}
