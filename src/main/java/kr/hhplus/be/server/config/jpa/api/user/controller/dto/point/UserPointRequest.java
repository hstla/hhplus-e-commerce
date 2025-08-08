package kr.hhplus.be.server.config.jpa.api.user.controller.dto.point;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPointRequest {

	public record ChargePoint(@NotNull @PositiveOrZero Long chargePoint) {
		public static ChargePoint of(Long chargePoint) {
			return new ChargePoint(chargePoint);
		}
	}
}