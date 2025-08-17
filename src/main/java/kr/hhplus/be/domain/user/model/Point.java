package kr.hhplus.be.domain.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kr.hhplus.be.global.error.RestApiException;
import kr.hhplus.be.global.error.UserErrorCode;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Point {
	private static final Long MIN_CHARGE_AMOUNT = 1_000L;
	private static final Long MAX_CHARGE_AMOUNT = 1_000_000L;

	@Column(name = "point", nullable = false)
	private Long amount;

	private Point(Long amount) {
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
		if (chargeAmount < MIN_CHARGE_AMOUNT ||  chargeAmount > MAX_CHARGE_AMOUNT) {
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