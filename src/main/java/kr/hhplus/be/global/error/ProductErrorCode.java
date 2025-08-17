package kr.hhplus.be.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
	INACTIVE_PRODUCT(HttpStatus.FORBIDDEN, "Product is inactive"),
	NOT_FOUND_PRODUCT_OPTION(HttpStatus.NOT_FOUND, "Product option not found"),
	OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "Product is out of stock"),
	INVALID_OPTION_NAME(HttpStatus.BAD_REQUEST, "Option name must not be blank"),
	INVALID_PRICE(HttpStatus.BAD_REQUEST, "Price must be 0 or more"),
	INVALID_STOCK(HttpStatus.BAD_REQUEST, "Stock quantity must be 0 or more"),
	INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "Product name must not be blank"),
	INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, "Product description must not be blank"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
