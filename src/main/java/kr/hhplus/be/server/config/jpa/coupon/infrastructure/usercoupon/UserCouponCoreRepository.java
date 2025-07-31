package kr.hhplus.be.server.config.jpa.coupon.infrastructure.usercoupon;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.infrastructure.mapper.CouponMapper;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCoupon;
import kr.hhplus.be.server.config.jpa.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponCoreRepository implements UserCouponRepository {

	private final JpaUserCouponRepository jpaUserCouponRepository;
	private final CouponMapper couponMapper;


	@Override
	public UserCoupon save(UserCoupon userCoupon) {
		UserCouponEntity savedEntity = jpaUserCouponRepository.save(couponMapper.toEntity(userCoupon));
		return couponMapper.toModel(savedEntity);
	}

	@Override
	public List<UserCoupon> findAllByUserId(Long userId) {
		List<UserCouponEntity> entities = jpaUserCouponRepository.findAllByUserId(userId);

		return entities.stream()
			.map(couponMapper::toModel)
			.toList();
	}

	@Override
	public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
		return jpaUserCouponRepository.findByUserIdAndCouponId(userId, couponId).isPresent();
	}

	@Override
	public UserCoupon findById(Long userCouponId) {
		UserCouponEntity userCouponEntity = jpaUserCouponRepository.findById(userCouponId)
			.orElseThrow(() -> new RestApiException(CouponErrorCode.NOT_FOUND_USER_COUPON));
		return couponMapper.toModel(userCouponEntity);
	}
}