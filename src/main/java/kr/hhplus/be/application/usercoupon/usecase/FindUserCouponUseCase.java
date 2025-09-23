package kr.hhplus.be.application.usercoupon.usecase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.application.usercoupon.dto.UserCouponResult;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindUserCouponUseCase {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;

	@Transactional(readOnly = true)
	public List<UserCouponResult.UserCouponInfo> execute(Long userId) {
		userRepository.assertUserExists(userId);
		List<UserCoupon> allByUserId = userCouponRepository.findAllByUserId(userId);

		List<UserCouponResult.UserCouponInfo> userCouponResults  = new ArrayList<>();
		for (UserCoupon userCoupon : allByUserId) {
			Coupon findCoupon = couponRepository.findById(userCoupon.getCouponId());
			UserCouponResult.UserCouponInfo userCouponInfo = UserCouponResult.UserCouponInfo.of(userCoupon, findCoupon);
			userCouponResults.add(userCouponInfo);
		}
		return userCouponResults;
	}
}