package kr.hhplus.be.global.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedSpinLock {
	/**
	 * 락 키들 (SpEL 표현식 지원) 단일, 멀티 둘다 지원 가능
	 */
	String[] keys();

	/**
	 * 락 보유 시간
	 */
	long holdTime() default 3L;

	/**
	 * 락 보유 시간 단위
	 */
	TimeUnit holdTimeUnit() default TimeUnit.SECONDS;

	/**
	 * 전체 대기 허용 시간 (밀리초)
	 */
	long maxWaitMillis() default 5000L;

	/**
	 * 백오프 초기 지연시간 (밀리초)
	 */
	long initialDelayMillis() default 100L;

	/**
	 * 백오프 최대 지연시간 (밀리초)
	 */
	long maxDelayMillis() default 1000L;
}