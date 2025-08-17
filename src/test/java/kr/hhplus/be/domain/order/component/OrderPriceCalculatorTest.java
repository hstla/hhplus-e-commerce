package kr.hhplus.be.domain.order.component;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderPriceCalculator 단위 테스트")
class OrderPriceCalculatorTest {

	@InjectMocks
	private OrderPriceCalculator orderPriceCalculator;

	@Nested
	@DisplayName("총 가격 계산 검증")
	class calculateTotalPriceTest {

		@Test
		@DisplayName("기존 가격과 할인 가격을 입력받아 성공적으로 계산한다")
		void calculateTotalPrice_success() {
			// given
			long originPrice = 10_000L;
			long discountPrice = 2_000L;

			// when
			long totalPrice = orderPriceCalculator.calculateTotalPrice(originPrice, discountPrice);

			// Then
			assertThat(totalPrice).isEqualTo(8_000L);
		}

		@Test
		@DisplayName("할인 가격이 기존 가격보다 높아 0을 반환한다")
		void calculateTotalPrice_return_zero() {
			// given
			long originPrice = 10_000L;
			long discountPrice = 11_000L;

			// when
			long totalPrice = orderPriceCalculator.calculateTotalPrice(originPrice, discountPrice);

			// Then
			assertThat(totalPrice).isEqualTo(0L);
		}
	}
}