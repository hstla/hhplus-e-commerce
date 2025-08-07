package kr.hhplus.be.server.config.jpa.user.domain.model;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Point {
	private static final Long MIN_CHARGE_AMOUNT = 1_000L;
	private static final Long MAX_CHARGE_AMOUNT = 1_000_000L;

	private final Long amount;

	private Point(Long amount) {
		if (amount < 0 || amount > MAX_CHARGE_AMOUNT) {
			throw new RestApiException(UserErrorCode.INVALID_USER_POINT);
		}
		this.amount = amount;
	}

	public static Point zero() {
		return new Point(0L);
	}

	public static Point of(Long amount) {
		return new Point(amount);
	}

	public Point charge(Long chargeAmount) {
		if (chargeAmount <= 0) {
			throw new RestApiException(UserErrorCode.INVALID_CHARGE_AMOUNT);
		}
		if (chargeAmount < MIN_CHARGE_AMOUNT) {
			throw new RestApiException(UserErrorCode.INVALID_USER_POINT);
		}
		return new Point(this.amount + chargeAmount);
	}

	public Point use(Long useAmount) {
		if (useAmount < 0) {
			throw new RestApiException(UserErrorCode.INVALID_PAY_AMOUNT);
		}
		if (isLessThan(useAmount)) {
			throw new RestApiException(UserErrorCode.INSUFFICIENT_USER_POINT);
		}
		return new Point(this.amount - useAmount);
	}

	public boolean isLessThan(Long value) {
		return this.amount < value;
	}
}