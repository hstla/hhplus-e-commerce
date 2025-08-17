package kr.hhplus.be.api.product.query;

import java.time.LocalDate;
import java.time.LocalDateTime;

import kr.hhplus.be.domain.order.infrastructure.JpaOrderProductRepository;
import kr.hhplus.be.domain.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.domain.order.model.Order;
import kr.hhplus.be.domain.order.model.OrderProduct;
import kr.hhplus.be.domain.order.model.ProductOptionSnapshot;
import kr.hhplus.be.domain.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;

public interface QueryTestDataSetUp {

	record TestData(Product product1, Product product2, LocalDateTime baseTime) {}

	default TestData setUpTestData(
		JpaProductOptionRepository productOptionRepository,
		JpaProductRepository productRepository,
		JpaOrderRepository orderRepository,
		JpaOrderProductRepository orderProductRepository
	) {
		// 데이터 초기화
		productOptionRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();

		LocalDateTime baseTime = LocalDate.now().atTime(12, 0);

		// 상품 등록
		Product product1 = Product.create("상품1", ProductCategory.CLOTHING, "설명1설명1설명11");
		productRepository.save(product1);
		Product product2 = Product.create("상품2", ProductCategory.CLOTHING, "설명2설명2설명22");
		productRepository.save(product2);

		ProductOption option1 = ProductOption.create(product1.getId(), "옵션1", 1000L, 10);
		ProductOption option2 = ProductOption.create(product1.getId(), "옵션2", 1000L, 10);
		ProductOption option3 = ProductOption.create(product2.getId(), "옵션3", 1000L, 10);
		productOptionRepository.save(option1);
		productOptionRepository.save(option2);
		productOptionRepository.save(option3);

		// 주문 등록
		LocalDateTime orderAt = baseTime.minusDays(2);
		Order order = Order.create(1L, null, 1000L, 0L, 1000L, orderAt);
		order = orderRepository.save(order);

		orderProductRepository.save(OrderProduct.create(order.getId(),
			ProductOptionSnapshot.create(option1.getId(), option1.getName(), 2, option1.getPrice())));
		orderProductRepository.save(OrderProduct.create(order.getId(),
			ProductOptionSnapshot.create(option2.getId(), option2.getName(), 4, option2.getPrice())));
		orderProductRepository.save(OrderProduct.create(order.getId(),
			ProductOptionSnapshot.create(option3.getId(), option3.getName(), 5, option3.getPrice())));

		return new TestData(product1, product2, baseTime);
	}
}