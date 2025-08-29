package kr.hhplus.be.global.common.aop;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import kr.hhplus.be.global.error.CommonErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Order(1) // 트랜잭션보다 먼저 실행
@RequiredArgsConstructor
@Slf4j
public class DistributedSpinLockAspect {

	private final RedissonClient redissonClient;
	private final LockKeyResolver lockKeyResolver;

	private static final String REDISSON_LOCK_PREFIX = "hhplus:lock:";

	@Around("@annotation(distributedSpinLock)")
	public Object around(ProceedingJoinPoint joinPoint, DistributedSpinLock distributedSpinLock) throws Throwable {
		List<String> lockKeys = lockKeyResolver.resolveLockKeys(joinPoint, distributedSpinLock.keys());

		// 단일락 and 멀티락 처리
		if (lockKeys.size() == 1) {
			return handleSingleSpinLock(joinPoint, distributedSpinLock, lockKeys.get(0));
		} else {
			return handleMultiSpinLock(joinPoint, distributedSpinLock, lockKeys);
		}
	}

	/**
	 * 단일 스핀락 처리
	 */
	private Object handleSingleSpinLock(ProceedingJoinPoint joinPoint, DistributedSpinLock annotation, String lockKey) throws Throwable {
		String fullLockKey = REDISSON_LOCK_PREFIX + lockKey;
		RLock spinLock = redissonClient.getSpinLock(fullLockKey);
		FullJitterBackOff backOff = new FullJitterBackOff(annotation.initialDelayMillis(), annotation.maxDelayMillis());
		long deadline = System.currentTimeMillis() + annotation.maxWaitMillis();
		int attempt = 0;

		try {
			while (System.currentTimeMillis() < deadline) {
				if (spinLock.tryLock(0, annotation.holdTime(), annotation.holdTimeUnit())) {
					try {
						log.debug("단일 스핀락 획득 성공: {}", fullLockKey);
						return joinPoint.proceed();
					} finally {
						if (spinLock.isHeldByCurrentThread()) {
							spinLock.unlock();
							log.debug("단일 스핀락 해제 완료: {}", fullLockKey);
						}
					}
				}

				long sleepTime = backOff.nextDelay(attempt++);
				Thread.sleep(sleepTime);
			}

			log.error("단일 스핀락 획득 실패 - 시간 초과: {}", fullLockKey);
			throw new RestApiException(CommonErrorCode.LOCK_ACQUIRE_FAILED);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("단일 스핀락 획득 중 인터럽트 발생: {}", fullLockKey);
			throw new RestApiException(CommonErrorCode.LOCK_ACQUIRE_FAILED);
		}
	}

	/**
	 * 멀티 스핀락 처리
	 */
	private Object handleMultiSpinLock(ProceedingJoinPoint joinPoint, DistributedSpinLock annotation, List<String> lockKeys) throws Throwable {
		// 데드락 방지를 위해 키를 정렬
		List<String> sortedKeys = lockKeys.stream()
			.map(key -> REDISSON_LOCK_PREFIX + key)
			.sorted()
			.toList();
		List<RLock> locks = sortedKeys.stream()
			.map(redissonClient::getSpinLock)
			.toList();

		RedissonMultiLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));
		FullJitterBackOff backOff = new FullJitterBackOff(annotation.initialDelayMillis(), annotation.maxDelayMillis());
		long deadline = System.currentTimeMillis() + annotation.maxWaitMillis();
		int attempt = 0;

		try {
			while (System.currentTimeMillis() < deadline) {
				if (multiLock.tryLock(0, annotation.holdTime(), annotation.holdTimeUnit())) {
					try {
						log.debug("멀티 스핀락 획득 성공: {}", sortedKeys);
						return joinPoint.proceed();
					} finally {
						if (multiLock.isHeldByCurrentThread()) {
							multiLock.unlock();
							log.debug("멀티 스핀락 해제 완료: {}", sortedKeys);
						}
					}
				}

				long sleepTime = backOff.nextDelay(attempt++);
				Thread.sleep(sleepTime);
			}

			log.error("멀티 스핀락 획득 실패 - 시간 초과: {}", sortedKeys);
			throw new RestApiException(CommonErrorCode.LOCK_ACQUIRE_FAILED);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("멀티 스핀락 획득 중 인터럽트 발생: {}", sortedKeys);
			throw new RestApiException(CommonErrorCode.LOCK_ACQUIRE_FAILED);
		}
	}
}