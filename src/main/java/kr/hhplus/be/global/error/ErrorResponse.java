package kr.hhplus.be.global.error;

import java.util.List;

import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
	private final String code;
	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final List<ValidationError> errors;

	@Getter
	@RequiredArgsConstructor
	public static class ValidationError {
		private final String field;
		private final String message;

		public static ValidationError of(final FieldError fieldError) {
			return new ValidationError(fieldError.getField(),fieldError.getDefaultMessage());
		}
	}
}