package kr.hhplus.be.server.config.jpa.api.product.query;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderProductRepository;
import kr.hhplus.be.server.config.jpa.order.infrastructure.JpaOrderRepository;
import kr.hhplus.be.server.config.jpa.order.model.Order;
import kr.hhplus.be.server.config.jpa.order.model.OrderProduct;
import kr.hhplus.be.server.config.jpa.order.model.ProductOptionSnapshot;
import kr.hhplus.be.server.config.jpa.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.server.config.jpa.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;

public abstract class QueryTestDataSetUp {

	@Autowired
	protected JpaProductOptionRepository productOptionRepository;
	@Autowired
	protected JpaProductRepository productRepository;
	@Autowired
	protected JpaOrderRepository orderRepository;
	@Autowired
	private JpaOrderProductRepository orderProductRepository;

	protected Product product1;
	protected Product product2;

	protected LocalDateTime baseTime;

	@BeforeEach
	void setUp() {
		// 데이터 초기화
		productOptionRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();

		baseTime = LocalDate.now().atTime(12, 0);

		/**
		 * 데이터 입력 설명
		 * 상품1 - 옵션1(재고 10), 옵션2(재고 10)
		 * 상품2 - 옵션3(재고 10)
		 * 주문(주문 날짜 2일전)
		 * 주문 상품 - 상품1의 옵션1(2개), 상품1의 옵션2(4개), 상품2의 옵션3(5개)
		 */
		product1 = Product.create("상품1", ProductCategory.CLOTHING, "설명1설명1설명11");
		productRepository.save(product1);
		product2 = Product.create("상품2", ProductCategory.CLOTHING, "설명2설명2설명22");
		productRepository.save(product2);

		ProductOption option1 = ProductOption.create(product1.getId(), "옵션1", 1000L, 10);
		ProductOption option2 = ProductOption.create(product1.getId(), "옵션2", 1000L, 10);
		ProductOption option3 = ProductOption.create(product2.getId(), "옵션3", 1000L, 10);
		productOptionRepository.save(option1);
		productOptionRepository.save(option2);
		productOptionRepository.save(option3);

		LocalDateTime orderAt = baseTime.minusDays(2);
		Order order = Order.create(1L, null, 1000L, 0L, 1000L, orderAt);
		order = orderRepository.save(order);

		OrderProduct orderProduct1 = OrderProduct.create(order.getId(), ProductOptionSnapshot.create(option1.getId(), option1.getName(), 2, option1.getPrice()));
		OrderProduct orderProduct2 = OrderProduct.create(order.getId(), ProductOptionSnapshot.create(option2.getId(), option2.getName(), 4, option2.getPrice()));
		OrderProduct orderProduct3 = OrderProduct.create(order.getId(), ProductOptionSnapshot.create(option3.getId(), option3.getName(), 5, option3.getPrice()));
		orderProductRepository.save(orderProduct1);
		orderProductRepository.save(orderProduct2);
		orderProductRepository.save(orderProduct3);
	}
}