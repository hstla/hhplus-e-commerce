package kr.hhplus.be.server.config.jpa.coupon.infrastructure.coupon;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

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
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.mapper.CouponMapper;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;
import kr.hhplus.be.server.config.jpa.error.CouponErrorCode;
import kr.hhplus.be.server.config.jpa.error.RestApiException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CouponCoreRepository.class, CouponMapper.class, TestcontainersConfig.class})
@ActiveProfiles("test")
@DisplayName("CouponCoreRepository 테스트")
class CouponCoreRepositoryTest {
	@Autowired
	private JpaCouponRepository jpaCouponRepository;
	@Autowired
	private CouponCoreRepository couponRepository;

	@BeforeEach
	void globalSetUp() {
		jpaCouponRepository.deleteAll();
	}

	@Nested
	@DisplayName("save 메서드 테스트")
	class SaveTests {

		private CouponEntity savedCouponEntity;

		@BeforeEach
		void setUp() {
			LocalDateTime expireAt = LocalDateTime.now().plusDays(10);
			CouponEntity testEntity = new CouponEntity(CouponType.FIXED, "2000원 할인 쿠폰", 2_000L, 10, expireAt);
			savedCouponEntity = jpaCouponRepository.save(testEntity);
		}

		@Test
		@DisplayName("새로운 Coupon 도메인 모델을 성공적으로 저장해야 한다")
		void save() {
			// given
			LocalDateTime expireAt = LocalDateTime.now().plusDays(10);
			Coupon newCoupon = Coupon.create(
				CouponType.FIXED,
				"1000원 할인 쿠폰",
				1_000L,
				10,
				expireAt
			);

			// when
			Coupon savedCoupon = couponRepository.save(newCoupon);

			// then
			assertThat(savedCoupon.getId()).isNotNull();
			assertThat(savedCoupon.getName()).isEqualTo("1000원 할인 쿠폰");
			assertThat(savedCoupon.getDiscountType()).isEqualTo(CouponType.FIXED);
			assertThat(savedCoupon.getDiscountValue()).isEqualTo(1_000L);
			assertThat(savedCoupon.getQuantity()).isEqualTo(10);
			assertThat(savedCoupon.getExpireAt()).isEqualTo(expireAt);
		}

		@Test
		@DisplayName("기존 Coupon 도메인 모델의 정보를 성공적으로 업데이트해야 한다")
		void save_update() {
			 // 쿠폰 이름하고 값만 변경
			// given
			Long saveCouponId = savedCouponEntity.getId();
			String updateName = "수정된 이름";
			long updateValue = 3_000L;
			Coupon updateCoupon = new Coupon(saveCouponId, savedCouponEntity.getDiscountType(), updateName, updateValue,
				savedCouponEntity.getQuantity(), savedCouponEntity.getExpireAt());

			// when
			Coupon updatedCoupon = couponRepository.save(updateCoupon);

			// then
			assertThat(updatedCoupon.getId()).isEqualTo(savedCouponEntity.getId());
			assertThat(updatedCoupon.getName()).isEqualTo(updateName);
			assertThat(updatedCoupon.getDiscountValue()).isEqualTo(updateValue);
			assertThat(updatedCoupon.getQuantity()).isEqualTo(savedCouponEntity.getQuantity());
			assertThat(updatedCoupon.getExpireAt()).isEqualTo(savedCouponEntity.getExpireAt());
		}
	}

	@Nested
	@DisplayName("findById 메서드 테스트")
	class FindByIdTests {

		private CouponEntity savedCouponEntity;

		@BeforeEach
		void setUp() {
			LocalDateTime expireAt = LocalDateTime.now().plusDays(10);
			CouponEntity testEntity = new CouponEntity(CouponType.FIXED, "2000원 할인 쿠폰", 2_000L, 10, expireAt);
			savedCouponEntity = jpaCouponRepository.save(testEntity);
		}

		@Test
		@DisplayName("존재하는 ID로 조회 시 Coupon 도메인 모델을 반환해야 한다")
		void findById() {
			// when
			Coupon foundCoupon = couponRepository.findById(savedCouponEntity.getId());

			// then
			assertThat(foundCoupon.getId()).isEqualTo(savedCouponEntity.getId());
			assertThat(foundCoupon.getName()).isEqualTo(savedCouponEntity.getName());
			assertThat(foundCoupon.getDiscountValue()).isEqualTo(savedCouponEntity.getDiscountValue());
			assertThat(foundCoupon.getQuantity()).isEqualTo(savedCouponEntity.getQuantity());
			assertThat(foundCoupon.getExpireAt()).isEqualTo(savedCouponEntity.getExpireAt());
		}

		@Test
		@DisplayName("존재하지 않는 ID로 조회 시 NOT_FOUND_COUPON 에러를 반환해야 한다")
		void findById_fail_not_found() {
			// given
			Long nonExistentId = 999L;

			// when then
			assertThatThrownBy(() -> couponRepository.findById(nonExistentId))
				.isInstanceOf(RestApiException.class)
				.hasMessage(CouponErrorCode.INACTIVE_COUPON.getMessage());
		}
	}
}