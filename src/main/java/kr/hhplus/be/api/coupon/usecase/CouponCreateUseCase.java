package kr.hhplus.be.api.coupon.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.api.coupon.usecase.dto.CouponCommand;
import kr.hhplus.be.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponStock;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.coupon.repository.CouponStockRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponCreateUseCase {

	private final CouponRepository couponRepository;
	private final CouponStockRepository couponStockRepository;

	@Transactional
	public CouponResult.CouponDetail execute(CouponCommand.CouponCreate command) {

		Coupon coupon = Coupon.create(
			command.name(),
			command.couponType(),
			command.discountValue(),
			command.initialStock(),
			command.expireAt()
		);

		Coupon savedCoupon = couponRepository.save(coupon);

		couponStockRepository.save(CouponStock.create(savedCoupon.getId(), command.initialStock()));

		return CouponResult.CouponDetail.of(savedCoupon);
	}
}