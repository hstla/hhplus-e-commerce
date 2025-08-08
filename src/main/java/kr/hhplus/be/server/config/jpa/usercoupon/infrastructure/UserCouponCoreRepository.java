package kr.hhplus.be.server.config.jpa.usercoupon.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCouponCoreRepository implements UserCouponRepository {

	private final JpaUserCouponRepository jpaUserCouponRepository;

	@Override
	public UserCoupon save(UserCoupon userCoupon) {
		return jpaUserCouponRepository.save(userCoupon);
	}

	@Override
	public List<UserCoupon> findAllByUserId(Long userId) {
		return jpaUserCouponRepository.findAllByUserId(userId);
	}

	@Override
	public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
		return jpaUserCouponRepository.existsByUserIdAndCouponId(userId, couponId);
	}

	@Override
	public UserCoupon findById(Long userCouponId) {
		return jpaUserCouponRepository.findById(userCouponId)
			.orElseThrow(() -> new RestApiException(CouponErrorCode.NOT_FOUND_USER_COUPON));
	}
}