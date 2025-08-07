package kr.hhplus.be.server.config.jpa.api.coupon.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponCommand;
import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponStock;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponStockRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponCreateUseCase {

	private final CouponRepository couponRepository;
	private final CouponStockRepository couponStockRepository;

	@Transactional
	public CouponResult.Info execute(CouponCommand.CouponCreate command) {
		CouponStock stock =  couponStockRepository.save(
			CouponStock.create(command.initialStock())
		);

		Coupon coupon = Coupon.create(
			command.name(),
			command.couponType(),
			command.discountValue(),
			command.initialStock(),
			command.expireAt(),
			stock.getId()
		);
		Coupon savedCoupon = couponRepository.save(coupon);

		return CouponResult.Info.of(savedCoupon);
	}
}