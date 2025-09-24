package kr.hhplus.be.application.product.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.order.model.OrderProduct;

@Repository
public interface RankRepository extends JpaRepository<OrderProduct,Long> {
	@Query("""
   SELECT new kr.hhplus.be.application.product.usecase.ProductRank2(
       p.id,
       p.name, 
       p.description, 
       p.category,
       SUM(op.productOptionVO.stock)
   )
   FROM OrderProduct op
       INNER JOIN ProductOption po ON op.productOptionVO.productOptionId = po.id
       INNER JOIN Product p ON po.productId = p.id
       INNER JOIN Order o ON o.id = op.orderId
   WHERE o.orderAt BETWEEN :startDate AND :endDate
       AND o.status IN ('AWAITING_PAYMENT', 'COMPLETED')
   GROUP BY p.id, p.name, p.description, p.category
   ORDER BY SUM(op.productOptionVO.stock) DESC
   """)
	List<ProductRank2> findTop5ByPeriod(
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		Pageable pageable
	);

}
