package kr.hhplus.be.global.common.aop;

import java.util.concurrent.ThreadLocalRandom;

public class FullJitterBackOff {
	private final long baseDelayMs;
	private final long maxDelayMs;

	public FullJitterBackOff(long baseDelayMs, long maxDelayMs) {
		this.baseDelayMs = baseDelayMs;
		this.maxDelayMs = maxDelayMs;
	}

	public long nextDelay(int attempt) {
		long expDelay = Math.min(maxDelayMs, baseDelayMs * (1L << attempt));
		return ThreadLocalRandom.current().nextLong(0, expDelay + 1);
	}
}