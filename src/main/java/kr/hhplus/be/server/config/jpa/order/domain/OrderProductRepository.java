package kr.hhplus.be.server.config.jpa.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderProductRepository {
	List<OrderProduct> findByOrderId(Long orderId);
}
