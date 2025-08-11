package kr.hhplus.be.server.config.jpa.api.user.controller.dto.point;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPointRequest {

	public record ChargePoint(@NotNull @Positive Long chargePoint) {
		public static ChargePoint of(Long chargePoint) {
			return new ChargePoint(chargePoint);
		}
	}
}