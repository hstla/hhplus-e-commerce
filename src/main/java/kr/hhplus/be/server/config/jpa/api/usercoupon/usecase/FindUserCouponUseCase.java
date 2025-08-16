package kr.hhplus.be.server.config.jpa.api.usercoupon.usecase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.usercoupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.CouponRepository;
import kr.hhplus.be.server.config.jpa.user.repository.UserRepository;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindUserCouponUseCase {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;

	@Transactional(readOnly = true)
	public List<CouponResult.UserCouponInfo> execute(Long userId) {
		userRepository.assertUserExists(userId);
		List<UserCoupon> allByUserId = userCouponRepository.findAllByUserId(userId);

		List<CouponResult.UserCouponInfo> userCouponResults  = new ArrayList<>();
		for (UserCoupon userCoupon : allByUserId) {
			Coupon findCoupon = couponRepository.findById(userCoupon.getCouponId());
			CouponResult.UserCouponInfo userCouponInfo = CouponResult.UserCouponInfo.of(userCoupon, findCoupon);
			userCouponResults.add(userCouponInfo);
		}
		return userCouponResults;
	}
}