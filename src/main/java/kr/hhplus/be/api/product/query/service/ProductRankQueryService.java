package kr.hhplus.be.api.product.query.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.api.product.query.dto.ProductRankDto;
import kr.hhplus.be.api.product.query.repository.ProductRankQueryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductRankQueryService {

	private final ProductRankQueryRepository productRankQueryRepository;

	@Cacheable(cacheNames = "productRank", key = "'top5'")
	@Transactional(readOnly = true)
	public List<ProductRankDto> getTop5ProductRank() {
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime end = today.minusDays(1).toLocalDate().atTime(LocalTime.MAX);
		LocalDateTime start = end.minusDays(3).toLocalDate().atTime(LocalTime.MIN);

		Pageable pages = PageRequest.of(0, 5);
		return productRankQueryRepository.findTop5ByPeriod(start, end, pages);
	}
}