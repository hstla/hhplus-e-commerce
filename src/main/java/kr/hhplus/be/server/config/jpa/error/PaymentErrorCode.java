package kr.hhplus.be.server.config.jpa.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
	INACTIVE_PAYMENT(HttpStatus.FORBIDDEN, "Payment is inactive"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
