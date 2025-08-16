package kr.hhplus.be.server.config.jpa.api.product.query.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.TestcontainersConfig;
import kr.hhplus.be.server.config.jpa.api.product.query.QueryTestDataSetUp;
import kr.hhplus.be.server.config.jpa.api.product.query.dto.ProductRankDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("ProductRankQueryRepository 슬라이스 테스트")
class ProductRankQueryRepositoryTest extends QueryTestDataSetUp {

	@Autowired
	private ProductRankQueryRepository productRankQueryRepository;

	@Test
	@DisplayName("3일간 총 판매량 기준 상위 5개 상품 조회")
	void findTop5ByPeriod_returnsCorrectOrder() {
		// given
		LocalDateTime baseTime = LocalDate.now().atTime(12, 0);
		LocalDateTime end = baseTime.minusDays(1).toLocalDate().atTime(23, 59, 59);
		LocalDateTime start = baseTime.minusDays(3).toLocalDate().atTime(0, 0, 0);

		// when
		List<ProductRankDto> top5 = productRankQueryRepository.findTop5ByPeriod(start, end, PageRequest.of(0, 5));

		// then
		log.info("top5: {}", top5);
		assertAll(
			() -> assertThat(top5).hasSize(2),
			() -> assertThat(top5.get(0).productId()).isEqualTo(product1.getId()),
			() -> assertThat(top5.get(0).totalSold()).isEqualTo(6L),
			() -> assertThat(top5.get(1).productId()).isEqualTo(product2.getId()),
			() -> assertThat(top5.get(1).totalSold()).isEqualTo(5L)
		);
	}
}