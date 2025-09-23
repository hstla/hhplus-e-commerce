package kr.hhplus.be.config;

import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ConcurrentTestSupport extends IntegrationTestConfig {

	protected <T> ConcurrentTestResult<T> runConcurrentTestWithIndex(
		int numberOfThreads,
		Function<Integer, T> task
	) throws InterruptedException {

		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);
		AtomicInteger threadCounter = new AtomicInteger(0);

		List<T> results = Collections.synchronizedList(new ArrayList<>());
		List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					int threadIndex = threadCounter.getAndIncrement();
					T result = task.apply(threadIndex);
					results.add(result);
				} catch (Throwable t) {
					exceptions.add(t);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		return new ConcurrentTestResult<>(results, exceptions);
	}

	protected <T> ConcurrentTestResult<T> runConcurrentTest(
		int numberOfThreads,
		Supplier<T> task
	) throws InterruptedException {

		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		List<T> results = synchronizedList(new ArrayList<>());
		List<Throwable> exceptions = synchronizedList(new ArrayList<>());

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					T result = task.get();
					synchronized (results) {
						results.add(result);
					}
				} catch (Throwable t) {
					synchronized (exceptions) {
						exceptions.add(t);
					}
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		return new ConcurrentTestResult<>(results, exceptions);
	}

	protected record ConcurrentTestResult<T>(
		List<T> results,
		List<Throwable> exceptions
	) { }
}