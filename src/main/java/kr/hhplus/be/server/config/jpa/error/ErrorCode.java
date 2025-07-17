package kr.hhplus.be.server.config.jpa.error;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	VALIDATION_ERROR("VALIDATION_ERROR", "유효성 검사에 실패했습니다."),
	INVALID_PARAMETER("INVALID_PARAMETER", "잘못된 요청 파라미터입니다."),
	TYPE_MISMATCH("TYPE_MISMATCH", "요청 파라미터 타입이 잘못되었습니다."),
	METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "알 수 없는 오류가 발생했습니다.");

	private final String code;
	private final String message;
}