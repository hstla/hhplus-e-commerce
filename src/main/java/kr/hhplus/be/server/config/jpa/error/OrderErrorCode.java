package kr.hhplus.be.server.config.jpa.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
	INACTIVE_ORDER(HttpStatus.FORBIDDEN, "Order is inactive"),
	CANNOT_PAY_NOT_CREATED_ORDER(HttpStatus.BAD_REQUEST, "Order can be paid only when it is in CREATED status"),
	ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "No products found for the order"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
