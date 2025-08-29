package kr.hhplus.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
public class LockConfiguration {

	@Bean
	public ExpressionParser expressionParser() {
		return new SpelExpressionParser();
	}
}
