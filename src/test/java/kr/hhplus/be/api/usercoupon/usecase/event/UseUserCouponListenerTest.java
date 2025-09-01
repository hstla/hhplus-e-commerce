package kr.hhplus.be.api.usercoupon.usecase.event;

import kr.hhplus.be.domain.shared.event.StockDecreasedEvent;
import kr.hhplus.be.config.IntegrationTestConfig;
import kr.hhplus.be.domain.coupon.infrastructure.JpaCouponRepository;
import kr.hhplus.be.domain.coupon.model.Coupon;
import kr.hhplus.be.domain.coupon.model.CouponType;
import kr.hhplus.be.domain.user.infrastructure.JpaUserRepository;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.usercoupon.infrastructure.JpaUserCouponRepository;
import kr.hhplus.be.domain.usercoupon.model.UserCoupon;
import kr.hhplus.be.domain.usercoupon.model.UserCouponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("UseUserCouponListener 통합 테스트")
class UseUserCouponListenerTest extends IntegrationTestConfig {

    @Autowired
    private UseUserCouponListener useUserCouponListener;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaCouponRepository couponRepository;

    @Autowired
    private JpaUserCouponRepository userCouponRepository;


    private User testUser;
    private Coupon fixedCoupon;
    private Coupon rateCoupon;
    private UserCoupon testUserFixedCoupon;
    private UserCoupon testUserRateCoupon;

    @BeforeEach
    void setUp() {
        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.create("tester", "tester@email.com", "password");
        userRepository.save(testUser);

        fixedCoupon = Coupon.create("정액 할인 쿠폰", CouponType.FIXED, 5000L, 100, LocalDateTime.now().plusDays(10));
        rateCoupon = Coupon.create("정률 할인 쿠폰", CouponType.PERCENT, 10L, 100, LocalDateTime.now().plusDays(10));
        couponRepository.saveAll(List.of(fixedCoupon, rateCoupon));

        testUserFixedCoupon = UserCoupon.publish(testUser.getId(), fixedCoupon.getId(), LocalDateTime.now());
        testUserRateCoupon = UserCoupon.publish(testUser.getId(), rateCoupon.getId(), LocalDateTime.now());
        userCouponRepository.saveAll(List.of(testUserFixedCoupon, testUserRateCoupon));
    }

    @Nested
    @DisplayName("정상 케이스")
    class SuccessCase {

        @Test
        @DisplayName("쿠폰ID가 null이면, 어떤 쿠폰도 사용 처리하지 않아야 한다")
        void shouldDoNothing_When_CouponIdIsNull() {
            // given
            StockDecreasedEvent event = new StockDecreasedEvent(1L, testUser.getId(), null, 50000L, List.of());

            // when
            useUserCouponListener.handleOrderCreated(event);

            // then
            UserCoupon coupon1 = userCouponRepository.findById(testUserFixedCoupon.getId()).orElseThrow();
            UserCoupon coupon2 = userCouponRepository.findById(testUserRateCoupon.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(coupon1.getStatus()).isEqualTo(kr.hhplus.be.domain.usercoupon.model.UserCouponStatus.ISSUED);
                softly.assertThat(coupon1.getUsedAt()).isNull();
                softly.assertThat(coupon2.getStatus()).isEqualTo(kr.hhplus.be.domain.usercoupon.model.UserCouponStatus.ISSUED);
                softly.assertThat(coupon2.getUsedAt()).isNull();
            });
        }

        @Test
        @DisplayName("정액 할인 쿠폰 사용 시, 해당 쿠폰이 '사용됨' 상태로 변경되어야 한다")
        void shouldChangeUserCouponToUsed_When_UsingFixedCoupon() {
            // given
            StockDecreasedEvent event = new StockDecreasedEvent(1L, testUser.getId(), testUserFixedCoupon.getId(), 50000L, List.of());

            // when
			long l = useUserCouponListener.handleOrderCreated(event);

            // then
            UserCoupon usedCoupon = userCouponRepository.findById(testUserFixedCoupon.getId()).get();

            assertSoftly(softly -> {
                softly.assertThat(usedCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
                softly.assertThat(usedCoupon.getUsedAt()).isNotNull();
            });
        }

        @Test
        @DisplayName("정률 할인 쿠폰 사용 시, 해당 쿠폰이 '사용됨' 상태로 변경되어야 한다")
        void shouldChangeUserCouponToUsed_When_UsingRateCoupon() {
            // given
            StockDecreasedEvent event = new StockDecreasedEvent(1L, testUser.getId(), testUserRateCoupon.getId(), 50000L, List.of());

            // when
            useUserCouponListener.handleOrderCreated(event);

            // then
            UserCoupon usedCoupon = userCouponRepository.findById(testUserRateCoupon.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(usedCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
                softly.assertThat(usedCoupon.getUsedAt()).isNotNull();
            });
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCase {

        @Test
        @DisplayName("존재하지 않는 쿠폰ID로 요청 시, 쿠폰 상태는 변하지 않아야 한다")
        void nullChangeCouponState() {
            // given
            long nonExistingUserCouponId = 9999L;
            StockDecreasedEvent event = new StockDecreasedEvent(1L, testUser.getId(), nonExistingUserCouponId, 50000L, List.of());

            // when
            useUserCouponListener.handleOrderCreated(event);

            // then
            UserCoupon coupon = userCouponRepository.findById(testUserFixedCoupon.getId()).orElseThrow();
            assertThat(coupon.getStatus()).isEqualTo(kr.hhplus.be.domain.usercoupon.model.UserCouponStatus.ISSUED);
        }

        @Test
        @DisplayName("다른 사용자의 쿠폰ID로 요청 시, 쿠폰 상태는 변하지 않아야 한다")
        void notChangeCouponState() {
            // given
            long anotherUserId = 999L;
            StockDecreasedEvent event = new StockDecreasedEvent(1L, anotherUserId, testUserFixedCoupon.getId(), 50000L, List.of());

            // when
            useUserCouponListener.handleOrderCreated(event);

            // then
            UserCoupon coupon = userCouponRepository.findById(testUserFixedCoupon.getId()).orElseThrow();
            assertThat(coupon.getStatus()).isEqualTo(kr.hhplus.be.domain.usercoupon.model.UserCouponStatus.ISSUED);
        }
    }
}