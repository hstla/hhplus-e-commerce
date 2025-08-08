package kr.hhplus.be.server.config.jpa.order.component;

import org.springframework.stereotype.Component;

@Component
public class OrderPriceCalculator {

	public long calculateTotalPrice(long originPrice, long discountPrice) {
		return Math.max(originPrice - discountPrice, 0L);
	}
}