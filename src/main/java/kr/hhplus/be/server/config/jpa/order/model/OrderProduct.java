package kr.hhplus.be.server.config.jpa.order.model;

import kr.hhplus.be.server.config.jpa.error.OrderErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderProduct {
	private Long id;
	private Long orderId;
	private Long productOptionId;
	private int quantity;
	private Long unitPrice;

	public static OrderProduct create(Long orderId, Long productOptionId, int quantity, Long unitPrice) {
		validateOptionId(productOptionId);
		validateQuantity(quantity);
		validateUnitPrice(unitPrice);
		return new OrderProduct(null, orderId, productOptionId, quantity, unitPrice);
	}

	private static void validateOptionId(Long productOptionId) {
		if (productOptionId == null) {
			throw new RestApiException(OrderErrorCode.OPTION_NOT_FOUND);
		}
	}

	private static void validateQuantity(int quantity) {
		if (quantity <= 0) {
			throw new RestApiException(OrderErrorCode.INVALID_ORDER_QUANTITY);
		}
	}

	private static void validateUnitPrice(Long unitPrice) {
		if (unitPrice < 0) {
			throw new RestApiException(OrderErrorCode.OPTION_NOT_PURCHASABLE);
		}
	}

	public long getCalculateAmount() {
		return quantity * unitPrice;
	}

	public void setOrderId(Long id) {
		this.orderId = id;
	}
}