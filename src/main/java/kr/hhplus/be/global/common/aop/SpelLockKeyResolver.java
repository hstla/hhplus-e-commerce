package kr.hhplus.be.global.common.aop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import kr.hhplus.be.global.error.CommonErrorCode;
import kr.hhplus.be.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpelLockKeyResolver implements LockKeyResolver {

	private final ExpressionParser expressionParser;

	// 의존성 주입을 통한 ExpressionParser 사용
	// public SpelLockKeyResolver(ExpressionParser expressionParser) {
	// 	this.expressionParser = expressionParser;
	// }

	@Override
	public List<String> resolveLockKeys(ProceedingJoinPoint joinPoint, String[] keyExpressions) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String[] parameterNames = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();

		StandardEvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		List<String> resolvedKeys = new ArrayList<>();
		for (String keyExpression : keyExpressions) {
			try {
				// prefix + spel 분리
				int spelStart = keyExpression.indexOf("#{");
				String prefix = "";
				String spel = keyExpression;

				if (spelStart >= 0) {
					prefix = keyExpression.substring(0, spelStart);
					spel = keyExpression.substring(spelStart + 2, keyExpression.length() - 1);
				}

				Expression expression = expressionParser.parseExpression(spel);
				Object value = expression.getValue(context);

				if (value instanceof String strVal) {
					resolvedKeys.add(prefix + strVal);
				} else if (value instanceof Collection<?> collection) {
					for (Object item : collection) {
						resolvedKeys.add(prefix + item);
					}
				} else if (value != null) {
					resolvedKeys.add(prefix + value.toString());
				}

			} catch (Exception e) {
				log.error("키 표현식 해석 실패: {}", keyExpression, e);
				throw new RestApiException(CommonErrorCode.LOCK_ACQUIRE_FAILED);
			}
		}

		if (resolvedKeys.isEmpty()) {
			log.error("유효한 락 키가 없습니다");
			throw new RestApiException(CommonErrorCode.LOCK_ACQUIRE_FAILED);
		}

		return resolvedKeys;
	}
}