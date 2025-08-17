package kr.hhplus.be.global.error;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import kr.hhplus.be.global.common.CommonResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RestApiException.class)
	public ResponseEntity<Object> handleCustomValidation(RestApiException e) {
		ErrorCode errorCode = e.getErrorCode();
		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentValidation(IllegalArgumentException e) {
		log.warn("handleIllegalArgumentValidation", e);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return handleExceptionInternal(errorCode, e.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request) {
		log.warn("handleIllegalArgumentValidation", ex);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return handleExceptionInternal(ex, errorCode);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(Exception ex) {
		log.warn("handleAllException", ex);
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return handleExceptionInternal(errorCode);
	}

	private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus()).body(CommonResponse.fail(makeErrorCodeBody(errorCode)));
	}

	private ErrorResponse makeErrorCodeBody(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.name(), errorCode.getMessage(), null);
	}

	private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String errorMessage) {
		return ResponseEntity.status(errorCode.getHttpStatus()).body(CommonResponse.fail(makeErrorCodeBody(errorCode, errorMessage)));
	}

	private ErrorResponse makeErrorCodeBody(ErrorCode errorCode, String errorMessage) {
		return new ErrorResponse(errorCode.name(), errorMessage, null);
	}

	private ResponseEntity<Object> handleExceptionInternal(BindException ex, ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus()).body(CommonResponse.fail(makeErrorCodeBody(ex, errorCode)));
	}

	private ErrorResponse makeErrorCodeBody(BindException ex, ErrorCode errorCode) {
		List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(ErrorResponse.ValidationError::of)
			.toList();
		return new ErrorResponse(errorCode.name(), errorCode.getMessage(), validationErrors);
	}
}