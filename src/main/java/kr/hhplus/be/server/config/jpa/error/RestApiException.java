package kr.hhplus.be.server.config.jpa.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException{
	private final ErrorCode errorCode;
}
