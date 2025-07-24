package kr.hhplus.be.server.config.jpa.coupon.domain.usercoupon;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.coupon.application.UserCouponCommand;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCouponService {

	private final UserCouponRepository userCouponRepository;

	public UserCouponInfo.Details addUserCoupon(UserCouponCommand.Publish command) {
		validateDuplicateCoupon(command.getUserId(), command.getCouponId());
		UserCoupon saveUserCoupon = userCouponRepository.save(UserCoupon.create(command.getUserId(), command.getCouponId()));
		return UserCouponInfo.Details.of(saveUserCoupon);
	}

	public List<UserCouponInfo.Details> findUserCoupon(Long userId) {
		List<UserCoupon> userCoupons = userCouponRepository.findAllByUserId(userId);

		if (userCoupons.isEmpty()) {
			return Collections.emptyList();
		}

		return userCoupons.stream().map(UserCouponInfo.Details::of).toList();
	}

	private void validateDuplicateCoupon(Long userId, Long couponId) {
		if (userCouponRepository.existsByUserAndCoupon(userId, couponId)) {
			throw new RestApiException(CouponErrorCode.DUPLICATED_COUPON);
		}
	}

	public Long useUserCoupon(Long userId, Long userCouponId) {
		UserCoupon userCoupon = userCouponRepository.findByIdAndUserId(userCouponId, userId)
			.orElseThrow(() -> new RestApiException(CouponErrorCode.NOT_FOUND_USER_COUPON));
		userCoupon.used();
		return userCoupon.getCouponId();
	}
}