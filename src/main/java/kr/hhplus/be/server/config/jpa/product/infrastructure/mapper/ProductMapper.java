package kr.hhplus.be.server.config.jpa.product.infrastructure.mapper;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.jpa.product.infrastructure.product.ProductEntity;
import kr.hhplus.be.server.config.jpa.product.infrastructure.productoption.ProductOptionEntity;
import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMapper {

	// Product mapper
	public ProductEntity toEntity(Product product) {
		return new ProductEntity(
			product.getId(),
			product.getName(),
			product.getCategory(),
			product.getDescription()
		);
	}

	public Product toModel(ProductEntity productEntity) {
		return new Product(
			productEntity.getId(),
			productEntity.getName(),
			productEntity.getCategory(),
			productEntity.getDescription()
		);
	}

	// ProductOption mapper
	public ProductOptionEntity toEntity(ProductOption option) {
		return new ProductOptionEntity(
			option.getId(),
			option.getProductId(),
			option.getOptionName(),
			option.getPrice(),
			option.getStock()
		);

	}

	public ProductOption toModel(ProductOptionEntity entity) {
		return new ProductOption(
			entity.getId(),
			entity.getProductId(),
			entity.getOptionName(),
			entity.getPrice(),
			entity.getStock()
		);
	}
}