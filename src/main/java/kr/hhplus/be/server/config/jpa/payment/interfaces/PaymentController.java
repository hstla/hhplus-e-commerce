package kr.hhplus.be.server.config.jpa.payment.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.payment.domain.PaymentStatus;
import kr.hhplus.be.server.config.jpa.payment.adapter.in.dto.CreatePaymentRequest;
import kr.hhplus.be.server.config.jpa.payment.adapter.in.dto.PaymentResponse;

@RestController
@RequestMapping("/api/payments")
public class PaymentController implements PaymentApiSpec {

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<PaymentResponse>> createPayment(CreatePaymentRequest createPaymentRequest) {
		return ResponseEntity.ok(CommonResponse.success(new PaymentResponse(1L,2L, PaymentStatus.PENDING, 1000)));
	}
}