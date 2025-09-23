package kr.hhplus.be.application.product.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.application.product.dto.ProductResult;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductOption;
import kr.hhplus.be.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindProductUseCase {

	private final ProductOptionRepository productOptionRepository;
	private final ProductRepository productRepository;

	public ProductResult.ProductOptionInfo findProductOptionsById(Long productId) {
		Product findProduct = productRepository.findById(productId);
		List<ProductOption> allByProductId = productOptionRepository.findAllByProductId(productId);

		return ProductResult.ProductOptionInfo.of(findProduct, allByProductId);
	}
}