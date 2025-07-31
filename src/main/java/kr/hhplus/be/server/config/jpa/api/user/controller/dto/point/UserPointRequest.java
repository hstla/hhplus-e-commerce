package kr.hhplus.be.server.config.jpa.api.user.controller.dto.point;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPointRequest {

	@Getter
	@NoArgsConstructor
	public static class ChargePoint {
		@NotNull @PositiveOrZero Long chargePoint;

		private ChargePoint(Long chargePoint) {
			this.chargePoint = chargePoint;
		}

		public static ChargePoint of(Long chargePoint) {
			return new ChargePoint(chargePoint);
		}
	}
}
