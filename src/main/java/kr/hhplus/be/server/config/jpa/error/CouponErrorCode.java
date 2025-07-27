package kr.hhplus.be.server.config.jpa.error;

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
	;

	private final HttpStatus httpStatus;
	private final String message;
}
