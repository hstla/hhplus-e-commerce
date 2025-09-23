package kr.hhplus.be.domain.common.event.dto;

public record OrderRequestItemInfo(
	Long productOptionId,
	int quantity
) {}