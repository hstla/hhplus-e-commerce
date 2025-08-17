package kr.hhplus.be.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
	INACTIVE_ORDER(HttpStatus.FORBIDDEN, "Order is inactive"),
	CANNOT_PAY_NOT_CREATED_ORDER(HttpStatus.BAD_REQUEST, "Order can be paid only when it is in CREATED status"),
	ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "No products found for the order"),
	INVALID_ORDER_QUANTITY(HttpStatus.BAD_REQUEST, "Product quantity must be at least 1"),
	OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Requested product option does not exist"),
	OPTION_NOT_PURCHASABLE(HttpStatus.BAD_REQUEST, "Requested product option is not available for purchase"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
