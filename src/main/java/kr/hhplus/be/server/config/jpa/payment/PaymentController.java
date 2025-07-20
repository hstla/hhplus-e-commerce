package kr.hhplus.be.server.config.jpa.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.payment.dto.CreatePaymentRequest;
import kr.hhplus.be.server.config.jpa.payment.dto.PaymentResponse;

@RestController
@RequestMapping("/api/payments")
public class PaymentController implements PaymentApiSpec {

	@Override
	@PostMapping
	public ResponseEntity<PaymentResponse> createPayment(CreatePaymentRequest createPaymentRequest) {
		return ResponseEntity.ok(new PaymentResponse(1L,2L, PaymentStatus.PENDING, 1000));
	}
}