package kr.hhplus.be.server.config.jpa.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.product.model.ProductOption;
import kr.hhplus.be.server.config.jpa.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductOptionRepository productOptionRepository;

	@Transactional
	public List<ProductOption> orderProductOptions(List<ProductInput.order> orderItemRequests) {
		List<ProductOption> options = new ArrayList<>();
		for (ProductInput.order orderItem : orderItemRequests) {
			ProductOption findOption = productOptionRepository.findById(orderItem.getProductOptionId());
			findOption.order(orderItem.getQuantity());
			options.add(findOption);
		}

		return options;
	}
}