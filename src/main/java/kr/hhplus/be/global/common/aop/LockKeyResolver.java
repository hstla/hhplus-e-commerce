package kr.hhplus.be.global.common.aop;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;

public interface LockKeyResolver {
	List<String> resolveLockKeys(ProceedingJoinPoint joinPoint, String[] keyExpressions);
}
