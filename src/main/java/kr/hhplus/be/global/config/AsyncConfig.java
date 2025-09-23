package kr.hhplus.be.global.config;

import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	@Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
	public ThreadPoolTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(32);
		executor.setMaxPoolSize(32);
		executor.setThreadNamePrefix("async");
		executor.setQueueCapacity(1000000);
		executor.setAwaitTerminationSeconds(5);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setTaskDecorator(new MdcTaskDecorator());
		executor.initialize();
		executor.getThreadPoolExecutor().prestartAllCoreThreads();
		return executor;
	}

	@Override
	public Executor getAsyncExecutor() {
		return asyncTaskExecutor();
	}

	private static class MdcTaskDecorator implements TaskDecorator {

		@Override
		public Runnable decorate(Runnable runnable) {
			Map<String, String> contextMap = MDC.getCopyOfContextMap();
			return () -> {
				try {
					if (contextMap != null) {
						MDC.setContextMap(contextMap);
					}
					runnable.run();
				} finally {
					MDC.clear();
				}
			};
		}
	}
}