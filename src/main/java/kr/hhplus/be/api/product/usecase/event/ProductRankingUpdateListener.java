package kr.hhplus.be.api.product.usecase.event;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.api.shared.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRankingUpdateListener {

	private final RedisTemplate<String, String> redisTemplate;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");

	@Async
	@TransactionalEventListener
	public void updateProductRankingCache(OrderCompletedEvent event) {
		String todayKey = "hhplus:cache:product:sales:" + LocalDate.now().format(FORMATTER);

		for (Map.Entry<Long, Integer> entry : event.productOrderCounts().entrySet()) {
			redisTemplate.opsForZSet().incrementScore(todayKey, String.valueOf(entry.getKey()), entry.getValue());
		}
		// TTL 3일 설정
		redisTemplate.expireAt(todayKey, LocalDate.now().plusDays(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
}