package kr.hhplus.be.api.product.query.repository;

import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import kr.hhplus.be.api.product.query.QueryTestDataSetUp;
import kr.hhplus.be.api.product.query.dto.ProductRankResponse;
import kr.hhplus.be.config.RepositoryTestConfig;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderProductRepository;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.domain.product.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("ProductRankQueryRepository 슬라이스 테스트")
class ProductRankQueryRepositoryTest extends RepositoryTestConfig implements QueryTestDataSetUp {

	@Autowired
	private ProductRankQueryRepository productRankQueryRepository;
	@Autowired
	private JpaProductOptionRepository productOptionRepository;
	@Autowired
	private JpaProductRepository productRepository;
	@Autowired
	private JpaOrderRepository orderRepository;
	@Autowired
	private JpaOrderProductRepository orderProductRepository;

	private Product product1;
	private Product product2;
	private LocalDateTime baseTime;

	@BeforeEach
	void setUp() {
		TestData testData = setUpTestData(productOptionRepository, productRepository, orderRepository, orderProductRepository);
		this.product1 = testData.product1();
		this.product2 = testData.product2();
		this.baseTime = testData.baseTime();
	}

	@Test
	@DisplayName("3일간 총 판매량 기준 상위 5개 상품 조회")
	void findTop5ByPeriod_returnsCorrectOrder() {
		// given
		LocalDateTime baseTime = LocalDate.now().atTime(12, 0);
		LocalDateTime end = baseTime.minusDays(1).toLocalDate().atTime(23, 59, 59);
		LocalDateTime start = baseTime.minusDays(3).toLocalDate().atTime(0, 0, 0);

		// when
		List<ProductRankResponse> top5 = productRankQueryRepository.findTop5ByPeriod(start, end, PageRequest.of(0, 5));

		// then
		log.info("top5: {}", top5);
		assertSoftly(soft -> {
			soft.assertThat(top5).hasSize(2);
			soft.assertThat(top5.get(0).productId()).isEqualTo(product1.getId());
			soft.assertThat(top5.get(0).totalSold()).isEqualTo(6L);
			soft.assertThat(top5.get(1).productId()).isEqualTo(product2.getId());
			soft.assertThat(top5.get(1).totalSold()).isEqualTo(5L);
		});
	}
}