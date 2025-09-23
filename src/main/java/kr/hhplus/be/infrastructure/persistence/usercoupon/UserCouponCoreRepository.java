package kr.hhplus.be.infrastructure.persistence.usercoupon;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.repository.UserCouponRepository;
import kr.hhplus.be.global.error.CouponErrorCode;
import kr.hhplus.be.global.error.RestApiException;
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