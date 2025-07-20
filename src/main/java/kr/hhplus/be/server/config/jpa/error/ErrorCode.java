package kr.hhplus.be.server.config.jpa.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String name();
	String getMessage();
	HttpStatus getHttpStatus();
}