package kr.hhplus.be.server.config.jpa.order.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.order.adapter.in.dto.CreateOrderRequest;
import kr.hhplus.be.server.config.jpa.order.adapter.in.dto.OrderResponse;

/**
 * 4️⃣ **`주요`** **주문 / 결제 API**
 *
 * - 사용자 식별자와 (상품 ID, 수량) 목록을 입력받아 주문하고 결제를 수행하는 API 를 작성합니다.
 * - 결제는 기 충전된 잔액을 기반으로 수행하며 성공할 시 잔액을 차감해야 합니다.
 * - 데이터 분석을 위해 결제 성공 시에 실시간으로 주문 정보를 데이터 플랫폼에 전송해야 합니다. ( 데이터 플랫폼이 어플리케이션 `외부` 라는 가정만 지켜 작업해 주시면 됩니다 )
 *
 * > 데이터 플랫폼으로의 전송 기능은 Mock API, Fake Module 등 다양한 방법으로 접근해 봅니다.
 */

@Tag(name="주문", description = "주문 관련 API")
public interface OrderApiSpec {

	@Operation(summary = "주문 생성", description = "상품, 상품 옵션, 유저 정보를 받아 주문을 생성합니다.")
	ResponseEntity<CommonResponse<OrderResponse>> createOrder(
		@Parameter(description = "상품 옵션, 아이디, 쿠폰 사용 여부", required = true) @RequestBody @Valid CreateOrderRequest createOrderRequest
	);
}