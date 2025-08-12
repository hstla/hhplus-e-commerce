package kr.hhplus.be.server.config.jpa.api.usercoupon.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.UserCouponCommand;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponStock;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponStockRepository;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.component.UserCouponValidator;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PublishCouponUseCase {

	private final UserRepository userRepository;
	private final UserCouponValidator userCouponValidator;
	private final CouponRepository couponRepository;
	private final CouponStockRepository couponStockRepository;
	private final UserCouponRepository userCouponRepository;

	@Transactional
	public CouponResult.CouponInfo execute(UserCouponCommand.Publish command) {
		Long userId = command.userId();
		userRepository.validateUserExists(userId);

		LocalDateTime now = LocalDateTime.now();
		Long couponId = command.couponId();

		Coupon findCoupon = couponRepository.findById(couponId);
		findCoupon.validateNotExpired(now);
		userCouponValidator.validateUserDoesNotHaveCoupon(userId, findCoupon.getId());

		//Lock
		CouponStock couponStock = couponStockRepository.findWithLockByCouponId(findCoupon.getId());
		couponStock.decreaseStock();
		couponStockRepository.save(couponStock);

		UserCoupon published = UserCoupon.publish(userId, findCoupon.getId(), now);
		userCouponRepository.save(published);
		return CouponResult.CouponInfo.of(findCoupon);
	}
}