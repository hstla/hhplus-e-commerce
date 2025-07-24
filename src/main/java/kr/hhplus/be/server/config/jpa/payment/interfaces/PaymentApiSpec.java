package kr.hhplus.be.server.config.jpa.payment.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.payment.adapter.in.dto.CreatePaymentRequest;
import kr.hhplus.be.server.config.jpa.payment.adapter.in.dto.PaymentResponse;

@Tag(name="결제", description = "결제 관련 API")
public interface PaymentApiSpec {

	@Operation(summary = "결제 생성", description = "주문번호를 받아 결제를 시도합니다.")
	ResponseEntity<CommonResponse<PaymentResponse>> createPayment(
		@Parameter(description = "주문 번호, 결제 방법", required = true) @RequestBody @Valid CreatePaymentRequest createPaymentRequest
	);
}
