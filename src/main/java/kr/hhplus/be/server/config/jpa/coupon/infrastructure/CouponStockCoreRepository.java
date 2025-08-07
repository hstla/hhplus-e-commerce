package kr.hhplus.be.server.config.jpa.coupon.infrastructure;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.model.CouponStock;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponStockRepository;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponStockCoreRepository implements CouponStockRepository {

	private final JpaCouponStockRepository jpaCouponStockRepository;

	@Override
	public CouponStock save(CouponStock couponStock) {
		return jpaCouponStockRepository.save(couponStock);
	}

	@Override
	public CouponStock findByIdLock(Long stockId) {
		return jpaCouponStockRepository.findByIdWithLock(stockId)
			.orElseThrow(() -> new RestApiException(CouponErrorCode.NOT_FOUND_COUPON_STOCK));
	}
}