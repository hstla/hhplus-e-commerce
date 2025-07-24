package kr.hhplus.be.server.config.jpa.product.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductRank extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_rank_id", unique = true)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	private int rank;
	private int totalSalesCount;
	private LocalDateTime snapshotDate;

	public ProductRank(Product product, int rank, int totalSalesCount, LocalDateTime snapshotDate) {
		this.product = product;
		this.rank = rank;
		this.totalSalesCount = totalSalesCount;
		this.snapshotDate = snapshotDate;
	}

	public static ProductRank createProductRank(Product product, int rank, int totalSalesCount, LocalDateTime snapshotDate) {
		return new ProductRank(product, rank, totalSalesCount, snapshotDate);
	}
}