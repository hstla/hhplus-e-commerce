package kr.hhplus.be.infrastructure.persistence.product;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import kr.hhplus.be.config.RepositoryTestConfig;
import kr.hhplus.be.domain.product.model.Product;
import kr.hhplus.be.domain.product.model.ProductCategory;
import kr.hhplus.be.domain.product.model.ProductOption;

@Import({ProductOptionCoreRepository.class})
@DisplayName("ProductOptionCoreRepository 테스트")
class ProductOptionCoreRepositoryTest extends RepositoryTestConfig {
	@Autowired
	private JpaProductOptionRepository jpaProductOptionRepository;
	@Autowired
	private JpaProductRepository jpaProductRepository;
	@Autowired
	private ProductOptionCoreRepository productOptionCoreRepository;

	private Long testProductId;

	@BeforeEach
	void globalSetUp() {
		jpaProductOptionRepository.deleteAll();
		jpaProductRepository.deleteAll();

		Product testProduct = jpaProductRepository.save(Product.create("메인 상품", ProductCategory.CLOTHING, "상품 설명은 10글자 이상이여야 합니다"));
		testProductId = testProduct.getId();
	}

	@Nested
	@DisplayName("findAllByProductId 메서드 테스트")
	class FindAllByProductIdTests {

		private Long anotherProductId;

		@BeforeEach
		void setUp() {
			Product anotherProduct = jpaProductRepository.save(Product.create("두번째 상품", ProductCategory.BEAUTY, "두번째 상품 설명12"));
			anotherProductId = anotherProduct.getId();

			// testProductId에 해당하는 옵션들 저장
			jpaProductOptionRepository.save(ProductOption.create(testProductId, "Small", 1_000L, 100));
			jpaProductOptionRepository.save(ProductOption.create(testProductId, "Large", 3_000L, 50));
			// anotherProductId에 해당하는 옵션 저장
			jpaProductOptionRepository.save(ProductOption.create(anotherProductId, "XLarge", 4_000L, 100));
		}

		@Test
		@DisplayName("특정 productId에 해당하는 모든 ProductOption 도메인 모델을 반환해야 한다")
		void findAllByProductId_success() {
			// when
			List<ProductOption> foundProductOptions = productOptionCoreRepository.findAllByProductId(testProductId);

			// then
			assertSoftly(soft -> {
				soft.assertThat(foundProductOptions).hasSize(2);
				soft.assertThat(foundProductOptions).extracting(ProductOption::getProductId).containsOnly(testProductId);
				soft.assertThat(foundProductOptions).extracting(ProductOption::getName).containsExactlyInAnyOrder("Small", "Large");
			});
		}

		@Test
		@DisplayName("존재하지 않는 productId로 조회 시 빈 리스트를 반환해야 한다")
		void findAllByProductId_empty() {
			// given
			Long nonExistentProductId = 999L;

			// when
			List<ProductOption> foundProductOptions = productOptionCoreRepository.findAllByProductId(nonExistentProductId);

			// then
			assertThat(foundProductOptions).isEmpty();
		}
	}
}