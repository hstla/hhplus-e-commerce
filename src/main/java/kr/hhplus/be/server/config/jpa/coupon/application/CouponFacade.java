package kr.hhplus.be.server.config.jpa.coupon.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponInfo;
import kr.hhplus.be.server.config.jpa.coupon.domain.coupon.CouponService;
import kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon.UserCouponInfo;
import kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon.UserCouponService;
import kr.hhplus.be.server.config.jpa.user.domain.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponFacade {

	private final CouponService couponService;
	private final UserService userService;
	private final UserCouponService userCouponService;

	public CouponResult.Coupon publish(CouponCommand.Publish command) {
		userService.validateExist(command.getUserId());
		CouponInfo.Info info = couponService.publishCoupon(command.getCouponId());
		userCouponService.addUserCoupon(UserCouponCommand.Publish.of(command.getUserId(), command.getCouponId()));
		return CouponResult.Coupon.of(info);
	}

	public List<CouponResult.UserCoupon> getUserCoupons(Long userId) {
		userService.validateExist(userId);
		List<UserCouponInfo.Details> userCoupons = userCouponService.findUserCoupon(userId);

		List<Long> couponIds = userCoupons.stream().map(UserCouponInfo.Details::getCouponId).toList();
		List<CouponInfo.Info> couponInfos = couponService.findCoupon(couponIds);

		Map<Long, CouponInfo.Info> couponInfoMap = couponInfos.stream()
			.collect(Collectors.toMap(CouponInfo.Info::getId, info -> info));

		return userCoupons.stream().map(uc -> {
			CouponInfo.Info couponInfo = couponInfoMap.get(uc.getCouponId());
			return CouponResult.UserCoupon.of(couponInfo, uc.getUsedAt());
			}).toList();
	}
}