package kr.hhplus.be.server.config.jpa.product.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.order.domain.OrderProductInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;


	public ProductInfo.ProductOptionInfo getProductOptionInfoById(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new RestApiException(ProductErrorCode.INACTIVE_PRODUCT));
		return ProductInfo.ProductOptionInfo.of(product.getId(), product.getName(), product.getCategory(), product.getDescription(), product.getOptions());
	}

	public List<ProductInfo.OptionInfo> getOptionsByProductIds(List<Long> productOptionIds) {
		productOptionIds.forEach(this::validateProductOptionId);

		List<ProductOption> options = productOptionRepository.findAllById(productOptionIds);
		return options.stream()
			.map(ProductInfo.OptionInfo::of)
			.toList();
	}

	public List<Product> findTop5Products() {
		return List.of();
	}

	private void validateProductOptionId(Long productOptionId) {
		boolean exists = productOptionRepository.existsById(productOptionId);
		if (!exists) {
			throw new RestApiException(ProductErrorCode.NOT_FOUND_PRODUCT_OPTION);
		}
	}

	// 상품 재고 확인 및 재고 감소
	@Transactional
	public void payProduct(List<OrderProductInfo.Info> orderProducts) {
		for (OrderProductInfo.Info product : orderProducts) {
			ProductOption option = productOptionRepository.findById(product.getProductOptionId())
				.orElseThrow(() -> new RestApiException(ProductErrorCode.NOT_FOUND_PRODUCT_OPTION));
			option.decreaseStock(product.getQuantity());
		}
	}
}