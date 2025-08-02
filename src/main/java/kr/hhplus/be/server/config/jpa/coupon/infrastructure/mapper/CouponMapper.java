package kr.hhplus.be.server.config.jpa.coupon.infrastructure.mapper;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.coupon.infrastructure.coupon.CouponEntity;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.usercoupon.UserCouponEntity;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.UserCoupon;

@Component
public class CouponMapper {

	/**
	 * coupon mapper
	 */
	public CouponEntity toEntity(Coupon coupon) {
		if (coupon == null) {
			return null;
		}
		return new CouponEntity(
			coupon.getId(),
			coupon.getDiscountType(),
			coupon.getName(),
			coupon.getDiscountValue(),
			coupon.getQuantity(),
			coupon.getExpireAt()
		);
	}

	public Coupon toModel(CouponEntity entity) {
		if (entity == null) {
			return null;
		}

		return new Coupon(
			entity.getId(),
			entity.getDiscountType(),
			entity.getName(),
			entity.getDiscountValue(),
			entity.getQuantity(),
			entity.getExpireAt()
		);
	}

	/**
	 * UserCoupon mapper
	 */
	public UserCouponEntity toEntity(UserCoupon userCoupon) {
		return new UserCouponEntity(
			userCoupon.getId(),
			userCoupon.getUserId(),
			userCoupon.getCouponId(),
			userCoupon.getStatus(),
			userCoupon.getIssuedAt(),
			userCoupon.getUsedAt()
		);
	}

	public UserCoupon toModel(UserCouponEntity entity) {
		return new UserCoupon(
			entity.getId(),
			entity.getUserId(),
			entity.getCouponId(),
			entity.getStatus(),
			entity.getIssuedAt(),
			entity.getUsedAt()
		);
	}
}