package kr.hhplus.be.server.config.jpa.product.dto;

public record ProductOptionResponse(
	String optionName,
	String description,
	long price,
	int quantity
){
}
