package kr.hhplus.be.server.config.jpa.error;

import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException{
	private final ErrorCode errorCode;

	public RestApiException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}