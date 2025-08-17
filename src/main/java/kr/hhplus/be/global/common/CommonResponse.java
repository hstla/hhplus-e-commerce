package kr.hhplus.be.global.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import kr.hhplus.be.global.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
	private Meta meta;
	private T data;

	public static <T> CommonResponse<T> success(T data) {
		return new CommonResponse<>(
			new Meta("SUCCESS", "", "성공적으로 처리하였습니다.", null),
			data
		);
	}

	public static <T> CommonResponse<T> fail(ErrorResponse error) {
		return new CommonResponse<>(
			new Meta("FAIL", error.getCode(), error.getMessage(), error.getErrors()),
			null
		);
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Meta{
		private String result;
		private String errorCode;
		private String message;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private List<ErrorResponse.ValidationError> errors;
	}
}