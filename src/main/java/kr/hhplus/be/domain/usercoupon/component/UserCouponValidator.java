package kr.hhplus.be.domain.usercoupon.component;

import org.springframework.stereotype.Component;

import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponValidator {

	private final UserCouponRepository userCouponRepository;

	public void validateUserDoesNotHaveCoupon(Long userId, Long couponId) {
		boolean exists = userCouponRepository.existsByUserIdAndCouponId(userId, couponId);

		if (exists) {
			throw new RestApiException(CouponErrorCode.DUPLICATED_COUPON);
		}
	}
}