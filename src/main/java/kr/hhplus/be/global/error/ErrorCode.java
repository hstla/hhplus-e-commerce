package kr.hhplus.be.global.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String name();
	String getMessage();
	HttpStatus getHttpStatus();
}