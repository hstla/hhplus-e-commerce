package kr.hhplus.be.domain.order.infrastructure;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.domain.order.model.Order;

public interface JpaOrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUserId(long userId);
}
