package kr.hhplus.be.api.product.query.dto;

public record ProductRankDto(
	Long productId,
	String productName,
	String description,
	Long totalSold
) {}