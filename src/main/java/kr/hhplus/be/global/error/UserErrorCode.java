package kr.hhplus.be.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
	INACTIVE_USER(HttpStatus.FORBIDDEN, "User is inactive"),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "Email already exists"),
	INVALID_USER_POINT(HttpStatus.BAD_REQUEST, "User point is out of the valid range."),
	INVALID_USER_NAME(HttpStatus.BAD_REQUEST, "User name must be between 2 and 10 characters."),
	INVALID_USER_EMAIL(HttpStatus.BAD_REQUEST, "User email must be a valid email format."),
	INVALID_USER_PASSWORD(HttpStatus.BAD_REQUEST, "User password must be between 5 and 20 characters."),
	INSUFFICIENT_USER_POINT(HttpStatus.BAD_REQUEST, "Insufficient user points"),
	INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "Charge amount must be a positive value."),
	INVALID_PAY_AMOUNT(HttpStatus.BAD_REQUEST, "Payment amount must be a positive value."),
	;

	private final HttpStatus httpStatus;
	private final String message;
}