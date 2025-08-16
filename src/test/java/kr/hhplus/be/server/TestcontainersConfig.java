package kr.hhplus.be.server;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class TestcontainersConfig {

	public static final MySQLContainer<?> MYSQL_CONTAINER;
	public static final GenericContainer<?> REDIS_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true)  // 컨테이너 재사용
			.waitingFor(Wait.forListeningPort())
			.withCommand("--character-set-server=utf8mb4",
				"--collation-server=utf8mb4_unicode_ci",
				"--skip-name-resolve");

		// 컨테이너 시작
		MYSQL_CONTAINER.start();

		// 시스템 프로퍼티 설정
		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl());
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
		System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");

		// Redis 컨테이너 설정
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.0.11"))
			.withExposedPorts(6379)
			.waitingFor(Wait.forListeningPort());
		REDIS_CONTAINER.start();

		String redisHost = REDIS_CONTAINER.getHost();
		Integer redisPort = REDIS_CONTAINER.getFirstMappedPort();
		System.setProperty("spring.redis.host", redisHost);
		System.setProperty("spring.redis.port", redisPort.toString());
	}
}