package kr.hhplus.be.server.config.jpa.usercoupon.component;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
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