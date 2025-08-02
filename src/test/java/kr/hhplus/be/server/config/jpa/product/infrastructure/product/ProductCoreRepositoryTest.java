package kr.hhplus.be.server.config.jpa.product.infrastructure.product;

import static org.assertj.core.api.Assertions.*;

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
import kr.hhplus.be.server.config.jpa.product.model.Product;
import kr.hhplus.be.server.config.jpa.product.model.ProductCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ProductCoreRepository.class, ProductMapper.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("ProductCoreRepositoryTest 테스트")
class ProductCoreRepositoryTest {
	@Autowired
	private JpaProductRepository jpaProductRepository;
	@Autowired
	private ProductCoreRepository productCoreRepository;

	@BeforeEach
	void globalSetUp() {
		jpaProductRepository.deleteAll();
	}

	@Nested
	@DisplayName("findById 메서드 테스트")
	class FindByIdTests {

		private ProductEntity savedProductEntity;

		@BeforeEach
		void setUp() {
			ProductEntity testEntity = new ProductEntity("새로운 상품", ProductCategory.CLOTHING, "설명설명");
			savedProductEntity = jpaProductRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 ID로 조회 시 Product 도메인 모델을 반환해야 한다")
		void findById_success() {
			// when
			Product foundProduct = productCoreRepository.findById(savedProductEntity.getId());

			// then
			assertThat(foundProduct.getId()).isEqualTo(savedProductEntity.getId());
			assertThat(foundProduct.getName()).isEqualTo(savedProductEntity.getName());
			assertThat(foundProduct.getCategory()).isEqualTo(savedProductEntity.getCategory());
		}

		@Test
		@DisplayName("존재하지 않는 ID로 조회 시 INACTIVE_PRODUCT 에러를 반환해야 한다")
		void findById_fail() {
			// given
			Long nonExistentId = 999L;

			// when then
			assertThatThrownBy(() -> productCoreRepository.findById(nonExistentId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(ProductErrorCode.INACTIVE_PRODUCT.getMessage());
		}
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		@Test
		@DisplayName("새로운 Product 도메인 모델을 성공적으로 저장해야 한다")
		void save_success() {
			// given
			Product newProduct = Product.create("새로운 상품", ProductCategory.CLOTHING, "설명설명");

			// when
			Product savedProduct = productCoreRepository.save(newProduct);

			// then
			assertThat(savedProduct.getId()).isNotNull();
			assertThat(savedProduct.getName()).isEqualTo("새로운 상품");
			assertThat(savedProduct.getDescription()).isEqualTo("설명설명");
		}

		@Test
		@DisplayName("기존 Product 도메인 모델의 정보를 성공적으로 업데이트해야 한다")
		void save_update() {
			// given
			ProductEntity existingEntity = new ProductEntity("기존 상품", ProductCategory.BEAUTY, "기존 설명");
			ProductEntity savedOriginalEntity = jpaProductRepository.save(existingEntity);

			Product updateProduct = new Product(savedOriginalEntity.getId(), "update name", ProductCategory.CLOTHING, "update desc");

			// when
			Product updatedProduct = productCoreRepository.save(updateProduct);

			// then
			assertThat(updatedProduct.getId()).isEqualTo(savedOriginalEntity.getId());
			assertThat(updatedProduct.getName()).isEqualTo("update name");
			assertThat(updatedProduct.getCategory()).isEqualTo(ProductCategory.CLOTHING);
			assertThat(updatedProduct.getDescription()).isEqualTo("update desc");
		}
	}
}