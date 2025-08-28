package kr.hhplus.be.api.product.usecase.event;

import kr.hhplus.be.api.shared.event.OrderCreatedEvent;
import kr.hhplus.be.api.shared.event.dto.OrderRequestItemInfo;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.product.infrastructure.JpaProductOptionRepository;
import kr.hhplus.be.domain.product.infrastructure.JpaProductRepository;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.*;

@DisplayName("StockEventListener 통합 테스트")
class StockEventListenerTest extends IntegrationTestConfig {

    @Autowired
    private StockEventListener stockEventListener;

    @Autowired
    private JpaProductRepository productRepository;

    @Autowired
    private JpaProductOptionRepository productOptionRepository;

    private Product product;
    private ProductOption testOption1;
    private ProductOption testOption2;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productOptionRepository.deleteAll();

		product = productRepository.save(Product.create("테스트 상품", ProductCategory.BEAUTY, "카테고리12121212"));
        testOption1 = productOptionRepository.save(ProductOption.create(product.getId(), "옵션1", 1000L, 50));
        testOption2 = productOptionRepository.save(ProductOption.create(product.getId(), "옵션2", 2000L, 50));
    }

    @Nested
    @DisplayName("정상 케이스")
    class SuccessCase {

        @Test
        @DisplayName("주문 생성 이벤트가 발행되면, 상품 재고가 주문 수량만큼 감소해야 한다")
        void decreaseStockSuccess() {
            // given
            long orderId = 1L;
            long userId = 1L;
            List<OrderRequestItemInfo> orderItems = List.of(
                new OrderRequestItemInfo(testOption1.getId(), 10),
                new OrderRequestItemInfo(testOption2.getId(), 20)
            );
            OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, null, orderItems);

            // when
			stockEventListener.handleOrderCreated(event);

            // then
            ProductOption updatedOption1 = productOptionRepository.findById(testOption1.getId()).get();
            ProductOption updatedOption2 = productOptionRepository.findById(testOption2.getId()).get();

			assertSoftly(soft -> {
				soft.assertThat(updatedOption1.getStock()).isEqualTo(40);
				soft.assertThat(updatedOption2.getStock()).isEqualTo(30);
			});
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCase {

        @Test
        @DisplayName("주문 수량이 재고보다 많을 경우, 재고는 변하지 않아야 한다")
        void notChangeStock() {
            // given
            long orderId = 2L;
            long userId = 1L;
            List<OrderRequestItemInfo> orderItems = List.of(
                new OrderRequestItemInfo(testOption1.getId(), 51) // 재고(50) 초과
            );
            OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, null, orderItems);

            // when
			stockEventListener.handleOrderCreated(event);

            // then
            ProductOption notChangedOption = productOptionRepository.findById(testOption1.getId()).get();
            assertThat(notChangedOption.getStock()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {

        @Test
        @DisplayName("여러 사용자가 동시에 주문해도, 재고는 정확하게 차감되어야 한다")
        void concurrentAccess() throws InterruptedException {
            // given
            int initialStock = testOption1.getStock(); // 50
            int threadCount = 55;
            int orderQuantityPerThread = 1;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            // when
            for (int i = 0; i < threadCount; i++) {
                long orderId = i + 100L;
                executorService.submit(() -> {
                    try {
                        List<OrderRequestItemInfo> orderItems = List.of(new OrderRequestItemInfo(testOption1.getId(), orderQuantityPerThread));
                        OrderCreatedEvent event = new OrderCreatedEvent(orderId, 1L, null, orderItems);
						stockEventListener.handleOrderCreated(event);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(10, TimeUnit.SECONDS);
            executorService.shutdown();

            // then
            ProductOption finalOption = productOptionRepository.findById(testOption1.getId()).get();
            assertThat(finalOption.getStock()).isEqualTo(Math.max(initialStock - threadCount * orderQuantityPerThread, 0));
        }
    }
}