package kr.hhplus.be.global.common.redis;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKeyName {
	// ===== Static Keys =====
	COUPON_VALID_SET("coupon:valid_set", null),
	COUPON_DB_SYNC_QUEUE("coupon:db_sync_queue", null),
	COUPON_DB_DEAD_LETTER_QUEUE("coupon:db_dead_letter_queue", null),
	PRODUCT_SALES_RANKING_3DAYS("product:sales:3days", Duration.ofDays(1)),

	// ===== Dynamic Keys =====
	COUPON_ISSUE_REQUEST_LIMIT("coupon:issue:rate-limit:user:%d:coupon:%d", Duration.ofMinutes(3)),
	COUPON_ISSUED_USER_BITMAP("coupon:%d:issued-users", null),
	COUPON_ISSUE_QUEUE("coupon:%d:issue-queue", null),
	COUPON_STOCK_CACHE("coupon:%d:stock", null),
	PRODUCT_TODAY_SALES_RANKING("ranking:product:sales:daily:%s", Duration.ofDays(3)),
	COUPON_ISSUE_TASK_STATUS("coupon:task:issue:%s", Duration.ofMinutes(3)),
	;

	private static final String PREFIX = "hhplus:cache:";

	private final String key;
	private final Duration ttl;

	public String toKey(Object... args) {
		return String.format(PREFIX + key, args);
	}
}