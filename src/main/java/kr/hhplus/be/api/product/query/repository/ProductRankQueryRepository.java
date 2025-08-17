package kr.hhplus.be.api.product.query.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.hhplus.be.api.product.query.dto.ProductRankDto;
import kr.hhplus.be.domain.order.model.OrderProduct;

public interface ProductRankQueryRepository extends JpaRepository<OrderProduct, Long> {
	 @Query("""
		SELECT new kr.hhplus.be.api.product.query.dto.ProductRankDto(
			p.id, 
			p.name, 
			p.description, 
			SUM(op.productOptionVO.stock)
		)
		FROM OrderProduct op
			INNER JOIN ProductOption po ON op.productOptionVO.productOptionId = po.id
			INNER JOIN Product p ON po.productId = p.id
			INNER JOIN Order o ON o.id = op.orderId
		WHERE o.orderAt BETWEEN :startDate AND :endDate
		GROUP BY p.id, p.name, p.description
		ORDER BY SUM(op.productOptionVO.stock) DESC
	""")
	List<ProductRankDto> findTop5ByPeriod(
		@Param("startDate")LocalDateTime startDate,
		@Param("endDate")LocalDateTime endDate,
		 Pageable pageable
	 );
}