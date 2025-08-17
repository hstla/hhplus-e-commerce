package kr.hhplus.be.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
	INACTIVE_COUPON(HttpStatus.FORBIDDEN, "Coupon is inactive"),
	OUT_OF_STOCK_COUPON(HttpStatus.BAD_REQUEST, "Coupon is out of stock."),
	EXPIRED_COUPON(HttpStatus.BAD_REQUEST, "The coupon has expired."),
	DUPLICATED_COUPON(HttpStatus.CONFLICT, "Coupon has already been issued to this user."),
	ALREADY_USED_COUPON(HttpStatus.CONFLICT, "Coupon has already been used."),
	NOT_FOUND_USER_COUPON(HttpStatus.NOT_FOUND, "User coupon not found."),
	INVALID_PERCENT_DISCOUNT(HttpStatus.BAD_REQUEST, "Discount value for percent coupon cannot exceed 100."),
	INVALID_FIXED_DISCOUNT(HttpStatus.BAD_REQUEST, "Discount value for fixed coupon must be greater than 0."),
	INVALID_COUPON_TYPE(HttpStatus.BAD_REQUEST, "Invalid coupon type provided."),
	NOT_FOUND_COUPON_STOCK(HttpStatus.NOT_FOUND, "Coupon stock not found."),
	INVALID_COUPON_OWNERSHIP(HttpStatus.FORBIDDEN, "You are not the owner of this coupon."),
	;

	private final HttpStatus httpStatus;
	private final String message;
}