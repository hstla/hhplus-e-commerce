package kr.hhplus.be.server.config.jpa.payment.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.payment.application.PaymentFacade;
import kr.hhplus.be.server.config.jpa.payment.application.PaymentResult;
import kr.hhplus.be.server.config.jpa.payment.interfaces.dto.PaymentRequest;
import kr.hhplus.be.server.config.jpa.payment.interfaces.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentApiSpec {

	private final PaymentFacade paymentFacade;

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<PaymentResponse.Payment>> createPayment(PaymentRequest.Payment paymentRequest) {
		// PaymentResult.Pay payment = paymentFacade.pay(paymentRequest.toCommand());
		// return ResponseEntity.ok(CommonResponse.success(PaymentResponse.Payment.of(payment)));
		return null;
	}
}