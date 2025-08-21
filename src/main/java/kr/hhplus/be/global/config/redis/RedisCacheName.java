package kr.hhplus.be.global.config.redis;

public class RedisCacheName {
	public static final String PRODUCT_RANK = "product:rank";

	public static final String BASE_KEY_NAME = "hhplus:cache:";

	// Static Keys (Standalone Keys)
	public static final String VALID_COUPONS = BASE_KEY_NAME + "valid_coupons";
	public static final String DB_WRITE_QUEUE = BASE_KEY_NAME + "db_write_queue";
	public static final String DEAD_LETTER_QUEUE = BASE_KEY_NAME + "dead_letter_queue";

	// Dynamic Key Prefixes
	public static final String RATE_LIMIT_PREFIX = BASE_KEY_NAME + "rate_limit:";
	public static final String COUPON_ISSUED_PREFIX = BASE_KEY_NAME + "coupon_issued:";
	public static final String COUPON_QUEUE_PREFIX = BASE_KEY_NAME + "coupon_queue:";
	public static final String COUPON_STOCK_PREFIX = BASE_KEY_NAME + "coupon_stock:";
}