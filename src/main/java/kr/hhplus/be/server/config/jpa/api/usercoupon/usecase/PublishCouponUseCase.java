package kr.hhplus.be.server.config.jpa.api.coupon.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.UserCouponCommand;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.service.CouponService;
import kr.hhplus.be.server.config.jpa.user.component.UserValidator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PublishCouponUseCase {

	private final UserValidator userValidator;
	private final CouponService couponService;

	@Transactional
	public CouponResult.CouponInfo execute(UserCouponCommand.Publish command) {
		LocalDateTime now = LocalDateTime.now();
		userValidator.validateExistingUser(command.getUserId());

		Coupon coupon = couponService.publishCoupon(command.getUserId(), command.getCouponId(), now);
		return CouponResult.CouponInfo.of(coupon);
	}
}