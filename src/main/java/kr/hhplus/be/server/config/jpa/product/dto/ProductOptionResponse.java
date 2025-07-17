package kr.hhplus.be.server.config.jpa.product.dto;

public record ProductOptionResponse(
	long productOptionId,
	String optionName,
	String description,
	long price,
	int quantity
){
}
