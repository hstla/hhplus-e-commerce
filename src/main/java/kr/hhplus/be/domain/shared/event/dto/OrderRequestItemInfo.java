package kr.hhplus.be.domain.shared.event.dto;

public record OrderRequestItemInfo(
	Long productOptionId,
	int quantity
) {}