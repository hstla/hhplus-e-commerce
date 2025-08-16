package kr.hhplus.be.server.config.jpa.api.coupon.usecase;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponCommand;
import kr.hhplus.be.server.config.jpa.api.coupon.usecase.dto.CouponResult;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.JpaCouponRepository;
import kr.hhplus.be.server.config.jpa.coupon.infrastructure.JpaCouponStockRepository;
import kr.hhplus.be.server.config.jpa.coupon.model.Coupon;
import kr.hhplus.be.server.config.jpa.coupon.model.CouponType;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CouponCreateUseCase 통합 테스트")
class CouponCreateUseCaseTest {

	@Autowired
	private CouponCreateUseCase couponCreateUseCase;
	@Autowired
	private JpaCouponRepository couponRepository;
	@Autowired
	private JpaCouponStockRepository couponStockRepository;

	@BeforeEach
	void setUp() throws Exception {
		couponStockRepository.deleteAll();
		couponRepository.deleteAll();
	}

	@Nested
	@DisplayName("쿠폰 생성 테스트")
	class executeTest {

		@Test
		@DisplayName("정상적으로 쿠폰이 생성된다")
		void execute_success() throws Exception {
			// given
			LocalDateTime expireAt = LocalDateTime.now().plusDays(2);
			String name = "couponName";
			CouponCommand.CouponCreate couponCreate = CouponCommand.CouponCreate.of(name, CouponType.FIXED, 1_000L, 10, expireAt);

			// when
			CouponResult.CouponDetail couponInfo = couponCreateUseCase.execute(couponCreate);
			Coupon coupon = couponRepository.findById(couponInfo.couponId()).get();

			// then
			assertThat(couponInfo.couponId()).isNotNull();
			assertThat(couponInfo.name()).isEqualTo(name);
			assertThat(couponInfo.expireAt()).isEqualTo(expireAt);

			// db에서 확인
			assertThat(coupon.getId()).isEqualTo(couponInfo.couponId());
			assertThat(coupon.getName()).isEqualTo(name);
		}
	}
}