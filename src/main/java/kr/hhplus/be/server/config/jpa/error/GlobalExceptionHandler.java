package kr.hhplus.be.server.config.jpa.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

	// @Valid 실패 - DTO
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldError().getDefaultMessage();
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(ErrorCode.VALIDATION_ERROR.getCode(), message));
	}

	// @RequestParam, @PathVariable 등의 제약 조건 실패
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(ErrorCode.INVALID_PARAMETER.getCode(),ErrorCode.INVALID_PARAMETER.getMessage()));
	}

	// PathVariable 타입 불일치
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse(ErrorCode.TYPE_MISMATCH.getCode(), ErrorCode.TYPE_MISMATCH.getMessage()));
	}

	// 지원하지 않는 HTTP 메서드
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED.getCode(),ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
	}

	// 예상하지 못한 모든 예외
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error(ex);
		return ResponseEntity.internalServerError()
			.body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),ErrorCode.INVALID_PARAMETER.getMessage()));
	}
}