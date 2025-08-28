package kr.hhplus.be.api.shared.event.dto;

public record OrderRequestItemInfo(
	Long productOptionId,
	int quantity
) {}