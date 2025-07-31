package kr.hhplus.be.server.config.jpa.product.infrastructure.productoption;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.TestcontainersConfig;
import kr.hhplus.be.server.config.jpa.error.ProductErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.product.infrastructure.mapper.ProductMapper;
import kr.hhplus.be.server.config.jpa.product.infrastructure.product.JpaProductRepository;
import kr.hhplus.be.server.config.jpa.product.infrastructure.product.ProductEntity;
import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;
import kr.hhplus.be.server.config.jpa.product.model.ProductOption;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ProductOptionCoreRepository.class, ProductMapper.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("ProductOptionCoreRepository 테스트")
class ProductOptionCoreRepositoryTest {
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

		ProductEntity testProductEntity = new ProductEntity("메인 상품", ProductCategory.CLOTHING, "상품 설명");
		testProductEntity = jpaProductRepository.save(testProductEntity);
		testProductId = testProductEntity.getId();
	}

	@Nested
	@DisplayName("findById 메서드 테스트")
	class FindByIdTests {

		private ProductOptionEntity savedProductOptionEntity;

		@BeforeEach
		void setUp() {
			ProductOptionEntity testEntity = new ProductOptionEntity(testProductId, "레드 컬러", 10_000L, 100);
			savedProductOptionEntity = jpaProductOptionRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 ID로 조회 시 ProductOption 도메인 모델을 반환해야 한다")
		void findById_success() {
			// when
			ProductOption foundProductOption = productOptionCoreRepository.findById(savedProductOptionEntity.getId());

			// then
			assertThat(foundProductOption.getId()).isEqualTo(savedProductOptionEntity.getId());
			assertThat(foundProductOption.getProductId()).isEqualTo(testProductId);
			assertThat(foundProductOption.getOptionName()).isEqualTo("레드 컬러");
			assertThat(foundProductOption.getStock()).isEqualTo(100);
			assertThat(foundProductOption.getPrice()).isEqualTo(10_000L);
		}

		@Test
		@DisplayName("존재하지 않는 ID로 조회 시 NOT_FOUND_PRODUCT_OPTION 에러를 반환해야 한다")
		void findById_fail_notFoundProductOption() {
			// given
			Long nonExistentId = 999L;

			// when then
			assertThatThrownBy(() -> productOptionCoreRepository.findById(nonExistentId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT_OPTION.getMessage());
		}
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		@Test
		@DisplayName("새로운 ProductOption 도메인 모델을 성공적으로 저장해야 한다")
		void save_success() {
			// given
			ProductOption newProductOption = ProductOption.create(testProductId, "블루", 20_000L, 10);

			// when
			ProductOption savedProductOption = productOptionCoreRepository.save(newProductOption);

			// then
			assertThat(savedProductOption.getId()).isNotNull();
			assertThat(savedProductOption.getProductId()).isEqualTo(testProductId);
			assertThat(savedProductOption.getOptionName()).isEqualTo("블루");
			assertThat(savedProductOption.getStock()).isEqualTo(10);
			assertThat(savedProductOption.getPrice()).isEqualTo(20_000L);
		}

		@Test
		@DisplayName("기존 ProductOption 도메인 모델의 정보를 성공적으로 업데이트해야 한다")
		void save_update() {
			// given
			ProductOptionEntity existingEntity = new ProductOptionEntity(testProductId, "초록", 20_000L, 20);
			ProductOptionEntity savedOriginalEntity = jpaProductOptionRepository.save(existingEntity);

			ProductOption productOptionToUpdate = productOptionCoreRepository.findById(savedOriginalEntity.getId());
			productOptionToUpdate.order(5);

			// when
			ProductOption updatedProductOption = productOptionCoreRepository.save(productOptionToUpdate);

			// then
			assertThat(updatedProductOption.getId()).isEqualTo(savedOriginalEntity.getId());
			assertThat(updatedProductOption.getStock()).isEqualTo(15);
			assertThat(updatedProductOption.getPrice()).isEqualTo(20_000L);
		}
	}

	@Nested
	@DisplayName("findAllByProductId 메서드 테스트")
	class FindAllByProductIdTests {

		private Long anotherProductId;

		@BeforeEach
		void setUp() {
			ProductEntity anotherProductEntity = new ProductEntity("두번째 상품", ProductCategory.BEAUTY, "두번째 상품 설명");
			anotherProductEntity = jpaProductRepository.save(anotherProductEntity);
			anotherProductId = anotherProductEntity.getId();

			// testProductId에 해당하는 옵션들 저장
			jpaProductOptionRepository.save(new ProductOptionEntity(testProductId, "Small", 1_000L, 100));
			jpaProductOptionRepository.save(new ProductOptionEntity(testProductId, "Large", 3_000L, 50));
			// anotherProductId에 해당하는 옵션 저장
			jpaProductOptionRepository.save(new ProductOptionEntity(anotherProductId, "XLarge", 4_000L, 100));
		}

		@Test
		@DisplayName("특정 productId에 해당하는 모든 ProductOption 도메인 모델을 반환해야 한다")
		void findAllByProductId_success() {
			// when
			List<ProductOption> foundProductOptions = productOptionCoreRepository.findAllByProductId(testProductId);

			// then
			assertThat(foundProductOptions).hasSize(2);
			assertThat(foundProductOptions).extracting(ProductOption::getProductId).containsOnly(testProductId);
			assertThat(foundProductOptions).extracting(ProductOption::getOptionName).containsExactlyInAnyOrder("Small", "Large");
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