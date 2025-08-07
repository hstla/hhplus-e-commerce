package kr.hhplus.be.server.config.jpa.order.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ProductOptionSnapshot 단위 테스트")
class ProductOptionSnapshotTest {

	@Nested
	@DisplayName("개별 가격 계산")
	class calculateTotalPriceTest {

		@Test
		@DisplayName("개별 origin 가격을 계산한다")
		void calculateOriginPrice_success() {
			// given
			ProductOptionSnapshot snapshot = ProductOptionSnapshot.create("test product option", 5, 1_000L);

			// when
			long originPrice = snapshot.calculateOriginPrice();

			// Then
			assertThat(originPrice).isEqualTo(5_000L);
		}
	}

}