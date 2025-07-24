package kr.hhplus.be.server.config.jpa.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
	INACTIVE_PRODUCT(HttpStatus.FORBIDDEN, "Product is inactive"),
	NOT_FOUND_PRODUCT_OPTION(HttpStatus.NOT_FOUND, "Product option not found"),
	OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "Product is out of stock"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
