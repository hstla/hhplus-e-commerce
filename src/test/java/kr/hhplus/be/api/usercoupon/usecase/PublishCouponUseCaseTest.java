package kr.hhplus.be.api.usercoupon.usecase;

import static org.assertj.core.api.SoftAssertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.api.usercoupon.usecase.dto.UserCouponCommand;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.coupon.infrastructure.JpaCouponRepository;
import kr.hhplus.be.domain.coupon.infrastructure.JpaCouponStockRepository;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponStock;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.Point;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.usercoupon.infrastructure.JpaUserCouponRepository;
import kr.hhplus.be.global.error.CouponErrorCode;

@DisplayName("PublishCouponUseCase 동시성 통합 테스트")
class PublishCouponUseCaseTest extends IntegrationTestConfig {
	@Autowired
	private PublishCouponUseCase publishCouponUseCase;
	@Autowired
	private JpaCouponRepository couponRepository;
	@Autowired
	private JpaCouponStockRepository couponStockRepository;
	@Autowired
	private JpaUserCouponRepository userCouponRepository;
	@Autowired
	private JpaUserRepository userRepository;

	private long couponId;
	private long couponStockId;
	private final List<Long> userIds = new ArrayList<>();

	@BeforeEach
	void setUp() throws Exception {
		couponRepository.deleteAll();
		couponStockRepository.deleteAll();
		userCouponRepository.deleteAll();
		userRepository.deleteAll();

		Coupon coupon = Coupon.create("할인쿠폰", CouponType.FIXED, 1_000L, 5, LocalDateTime.now().plusDays(1));
		couponId = couponRepository.save(coupon).getId();

		CouponStock stock = CouponStock.create(couponId, 5);
		CouponStock couponStock = couponStockRepository.save(stock);
		couponStockId = couponStock.getId();

		for (int i = 1; i <= 10; i++) {
			User user = new User(null, "User" + i, "user" + i + "@test.com", "password" + i, Point.zero());
			userIds.add(userRepository.save(user).getId());
		}
	}

	@Nested
	@DisplayName("publish 동시성 테스트")
	class Publish {

		@Test
		@DisplayName("동시에 여러 쓰레드가 쿠폰을 발급하면 재고는 0이고 발급된 수는 5여야 한다")
		void execute_success() throws InterruptedException {
			// given
			int numberOfThreads = 10;
			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch latch = new CountDownLatch(numberOfThreads);
			List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

			// when
			for (int i = 0; i < numberOfThreads; i++) {
				long userId = userIds.get(i);
				executorService.submit(() -> {
					try {
						UserCouponCommand.Publish command = UserCouponCommand.Publish.of(userId, couponId);
						publishCouponUseCase.execute(command);
					} catch (Exception e) {
						exceptions.add(e);
					} finally {
						latch.countDown();
					}
				});
			}

			latch.await();

			// then
			long issuedCount = userCouponRepository.count();
			CouponStock stock = couponStockRepository.findById(couponStockId).get();
			assertSoftly(soft -> {
				soft.assertThat(issuedCount).isEqualTo(5);
				soft.assertThat(stock.getStock()).isEqualTo(0);
				soft.assertThat(exceptions).hasSize(5);
				soft.assertThat(exceptions.get(0).getMessage()).isEqualTo(CouponErrorCode.OUT_OF_STOCK_COUPON.getMessage());
			});
		}
	}
}