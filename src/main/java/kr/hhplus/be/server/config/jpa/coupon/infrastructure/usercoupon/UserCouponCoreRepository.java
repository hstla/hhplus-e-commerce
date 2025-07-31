package kr.hhplus.be.server.config.jpa.coupon.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponCoreRepository implements UserCouponRepository {




	@Override
	public UserCoupon save(UserCoupon userCoupon) {
		return null;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean existsByUserAndCoupon(long userId, long couponId) {
		return false;
	}

	@Override
	public List<UserCoupon> findAllByUserId(Long userId) {
		return List.of();
	}

	@Override
	public Optional<UserCoupon> findByIdAndUserId(Long userId, Long userCouponId) {
		return Optional.empty();
	}
}