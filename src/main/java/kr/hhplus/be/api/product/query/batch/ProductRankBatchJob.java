package kr.hhplus.be.api.product.query.batch;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.api.product.query.dto.ProductRankDto;
import kr.hhplus.be.api.product.query.service.ProductRankQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRankBatchJob {

	private final ProductRankQueryService service;

	@Scheduled(cron = "0 10 0 * * ?")
	@CacheEvict(cacheNames = "productRank", key = "'top5'")
	public void updatedTop5Cache() {
		log.info("ProductRankBatchJob 시작");
		List<ProductRankDto> top5 = service.getTop5ProductRank();
		log.info("ProductRankBatchJob 완료, top5: {}", top5);
	}
}