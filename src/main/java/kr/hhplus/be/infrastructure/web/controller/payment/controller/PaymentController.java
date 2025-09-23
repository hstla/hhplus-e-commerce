package kr.hhplus.be.infrastructure.web.controller.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.application.payment.dto.PaymentResult;
import kr.hhplus.be.application.payment.usecase.CreatePaymentUseCase;
import kr.hhplus.be.global.common.CommonResponse;
import kr.hhplus.be.infrastructure.web.controller.payment.controller.dto.PaymentRequest;
import kr.hhplus.be.infrastructure.web.controller.payment.controller.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentApiSpec {

	private final CreatePaymentUseCase paymentUseCase;

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<PaymentResponse.Payment>> createPayment(PaymentRequest.Payment paymentRequest) {
		PaymentResult.Pay payment = paymentUseCase.execute(paymentRequest.toCommand());
		return ResponseEntity.ok(CommonResponse.success(PaymentResponse.Payment.of(payment)));
	}
}