package kr.hhplus.be.server.config.jpa.api.product.query.dto;

public record ProductRankDto(
	Long productId,
	String productName,
	String description,
	Long totalSold
) {}